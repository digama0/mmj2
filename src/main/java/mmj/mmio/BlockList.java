package mmj.mmio;

import mmj.lang.LangConstants;
import mmj.lang.LangException;

public class BlockList {
    private final StringBuilder blocks = new StringBuilder();
    public boolean marked = false;
    private int index = 0;

    public int getIndex() {
        return index;
    }

    public void addBlock(final String block) {
        blocks.append(block);
    }

    public int getNext(final String theoremLabel) throws LangException {
        if (index == blocks.length())
            return -1;

        final int blockLen = blocks.length();

        int decompressNbr = 0;

        while (true) {
            if (index >= blockLen)
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_PREMATURE_END, theoremLabel);

            final char nextChar = blocks.charAt(index++);
            if (nextChar >= LangConstants.COMPRESS_VALID_CHARS.length)
                throw new LangException(LangConstants.ERRMSG_COMPRESS_NOT_ASCII,
                    theoremLabel, index, nextChar);

            // translate 'A' to 0, 'B' to 1, etc. (1 is added to
            // 'A' thru 'Z' later -- curiously but effectively :)
            final byte nextCharCode = LangConstants.COMPRESS_VALID_CHARS[nextChar];

            if (nextCharCode == LangConstants.COMPRESS_ERROR_CHAR_VALUE)
                throw new LangException(LangConstants.ERRMSG_COMPRESS_BAD_CHAR,
                    theoremLabel, index, nextChar);

            if (nextCharCode == LangConstants.COMPRESS_UNKNOWN_CHAR_VALUE) {
                if (decompressNbr > 0)
                    throw new LangException(
                        LangConstants.ERRMSG_COMPRESS_BAD_UNK, theoremLabel,
                        index);
                return 0;
            }

            if (nextCharCode == LangConstants.COMPRESS_REPEAT_CHAR_VALUE)
                throw new LangException(LangConstants.ERRMSG_COMPRESS_BAD_RPT,
                    theoremLabel, index);

            if (nextCharCode >= LangConstants.COMPRESS_LOW_BASE) {
                decompressNbr = decompressNbr * LangConstants.COMPRESS_HIGH_BASE
                    + nextCharCode;
                continue;
            }

            // else...
            decompressNbr += nextCharCode + 1; // 'A' = 1 etc

            if (marked = index < blockLen
                && blocks.charAt(index) == LangConstants.COMPRESS_REPEAT_CHAR)
                index++;
            return decompressNbr;
        }
    }

    public boolean isEmpty() {
        return blocks.length() == 0;
    }

    @Override
    public String toString() {
        return blocks.toString();
    }
}
