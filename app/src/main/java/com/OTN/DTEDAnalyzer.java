package com.OTN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DTEDAnalyzer {
	
	/** offset into header where 4 char line count starts (4 char sample point follows) */
    private static final int _NUM_LNG_LINES_OFFSET = 47;
    private static final int _HEADER_OFFSET = 3428;
    private static final int _DATA_RECORD_PREFIX_SIZE = 8;
    private static final int _DATA_RECORD_SUFFIX_SIZE = 4;
	
	private RandomAccessFile raf = null;
	
	public DTEDAnalyzer(File f) {
		try {
			raf = new RandomAccessFile(f, "r");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double _getHeight(double latitude, double longitude){
		try {	
			raf.skipBytes(_NUM_LNG_LINES_OFFSET);
			byte[] bytes = {
					0, 0, 0, 0, 0, 0, 0, 0
			};

			if (raf.read(bytes, 0, 8) < 8) {
				// read did not get all of the information required
				throw new IOException("invalid file");
			}

			String lngLinesStr = new String(bytes, 0, 4, StandardCharsets.US_ASCII);
			String latPointsStr = new String(bytes, 4, 4,
					StandardCharsets.US_ASCII);

			int lngLines;
			int latPoints;

			try {
				lngLines = Integer.parseInt(lngLinesStr);
			} catch (Exception e) {
				throw new IOException(e);
			}
			try {
				latPoints = Integer.parseInt(latPointsStr);
			} catch (Exception e) {
				throw new IOException(e);
			}

			double latRatio = latitude - Math.floor(latitude);
			double lngRatio = longitude - Math.floor(longitude);

			double yd = latRatio * (latPoints - 1);
			double xd = lngRatio * (lngLines - 1);

			int x = (int) xd;
			int y = (int) yd;

			int dataRecSize = _DATA_RECORD_PREFIX_SIZE + (latPoints * 2)
					+ _DATA_RECORD_SUFFIX_SIZE;

			int byteOffset = (_HEADER_OFFSET - _NUM_LNG_LINES_OFFSET - 8)
					+ x * dataRecSize
					+ _DATA_RECORD_PREFIX_SIZE
					+ y * 2;

			int skipped;
			do {
				skipped = raf.skipBytes(byteOffset);
				byteOffset -= skipped;

				// three exit conditions, I want to know which one we hit
				if (skipped == 0) { // ran out of file
					break;
				} else if (byteOffset == 0) { // perfect
					break;
				} else if (byteOffset < 0) { // overshot
					break;
				}
			} while (true);

			return _readAndInterp(raf, dataRecSize, xd - x, yd - y);
		} catch (Exception e) {
            //TODO Fai qualcosa
        }	
		return Double.NaN;
	}
	
	
	/**
     * Interprets a raw 16-bit DTED sample into a float-point
     * elevation value.
     * <P>
     *  The negative in DTED is NOT two's complement,
     *  it's signed-magnitude. Mask off the MSB and multiple by
     * -1 to make it 2's complement.
     * @param s is the value.
     * @return  a double that represents the value correctly
     * interpretting the sign magnitude or {@link Double#NaN}
     * in the event that the value is null or invalid per
     * MIL-PRF-89020B
     */
    private static double interpretSample(final short s) {
        if (((s & 0xFFFF) == 0xFFFF))
            return Float.NaN;

        final float val = (1 - (2 * ((s & 0x8000) >> 15))) * (s & 0x7FFF);

        // XXX - per MIL-PRF89020B 3.11.2, elevation values should never exceed
        //       these values in practice
        if ((val < -12000) || (val > 9000))
            return Float.NaN;
        return val;
    }
	
	private static double _readAndInterp(final RandomAccessFile in,
            final int dataRecSize,
            final double xratio,
            final double yratio) throws IOException {

        double r = Double.NaN;

        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.BIG_ENDIAN);

        bb.put((byte) in.read());
        bb.put((byte) in.read());

        double sw = interpretSample(bb.getShort(0));

        bb.rewind();
        bb.put((byte) in.read());
        bb.put((byte) in.read());

        double nw = interpretSample(bb.getShort(0));

        in.skipBytes(dataRecSize - 4);
        bb.rewind();
        bb.put((byte) in.read());
        bb.put((byte) in.read());

        double se = interpretSample(bb.getShort(0));

        bb.rewind();
        bb.put((byte) in.read());
        bb.put((byte) in.read());

        double ne = interpretSample(bb.getShort(0));

        if (Double.isNaN(sw) &&
                Double.isNaN(nw) &&
                Double.isNaN(se) &&
                Double.isNaN(ne)) {

            return Double.NaN;
        }

        double mids;
        double midn;

        if (!Double.isNaN(nw) &&
                !Double.isNaN(ne) &&
                !Double.isNaN(se) &&
                !Double.isNaN(sw)) {
            mids = sw + (se - sw) * xratio;
            midn = nw + (ne - nw) * xratio;
        } else if (Double.isNaN(nw) &&
                !Double.isNaN(ne) &&
                !Double.isNaN(se) &&
                !Double.isNaN(sw)) {
            mids = sw + (se - sw) * xratio;
            midn = ne;
        } else if (!Double.isNaN(nw) &&
                Double.isNaN(ne) &&
                !Double.isNaN(se) &&
                !Double.isNaN(sw)) {
            mids = sw + (se - sw) * xratio;
            midn = nw;
        } else if (!Double.isNaN(nw) &&
                !Double.isNaN(ne) &&
                Double.isNaN(se) &&
                !Double.isNaN(sw)) {
            mids = sw;
            midn = nw + (ne - nw) * xratio;
        } else if (!Double.isNaN(nw) &&
                !Double.isNaN(ne) &&
                !Double.isNaN(se) &&
                Double.isNaN(sw)) {
            mids = se;
            midn = nw + (ne - nw) * xratio;
        } else {

            // XXX - consider interpolation as long as at least 2 values are
            //       non-null
            return r;
        }

        r = mids + (midn - mids) * yratio;

        return r;
    }
	
	public void close() {
		try {
			this.raf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
