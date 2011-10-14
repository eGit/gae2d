package org.fonteditor.utilities.random;

public class JUR {
  static final long SERIAL_VERSION_UID = 3905348978240129619L;
  private static final long MULTIPLIER = 0x5DEECE66DL;
  private static final long ADDEND = 0xBL;
  private static final long MASK = (1L << 48) - 1;
  private static final int BITS_PER_BYTE = 8;
  private static final int BYTES_PER_INT = 4;

  private long seed;
  private int stored;
  private int stored_bits_left = 0;

  private double next_next_gaussian;
  private boolean have_next_next_gaussian = false;

  public JUR() {
    this(System.currentTimeMillis());
  }

  public JUR(long seed) {
    setSeed(seed);
  }

  public void setSeed(long seed) {
    this.seed = (seed ^ MULTIPLIER) & MASK;
    have_next_next_gaussian = false;
  }

  protected int next(int bits) {
    long nextseed = (seed * MULTIPLIER + ADDEND) & MASK;

    seed = nextseed;
    return (int) (nextseed >>> (48 - bits));
  }

  public void nextBytes(byte[] bytes) {
    int number_requested = bytes.length;
    int number_got = 0;
    int rnd = 0;

    while (true) {
      for (int i = 0; i < BYTES_PER_INT; i++) {
        if (number_got == number_requested) {
          return;
        }
        rnd = (i == 0 ? next(BITS_PER_BYTE * BYTES_PER_INT) : rnd >> BITS_PER_BYTE);
        bytes[number_got++] = (byte) rnd;
      }
    }
  }

  public int nextInt() {
    return next(32);
  }

  public int nextInt(int n) {
    if (n <= 0) {
      throw new IllegalArgumentException("n must be positive");
    }
    if ((n & -n) == n) {
      return (int) ((n * (long) next(31)) >> 31);
    }
    int bits;
    int val;

    do {
      bits = next(31);
      val = bits % n;
    } while (bits - val + (n - 1) < 0);
    return val;
  }

  public long nextLong() {
    return ((long) (next(32)) << 32) + next(32);
  }

  public boolean nextBoolean() {
    return next(1) != 0;
  }

  public float nextFloat() {
    int i = next(24);

    return i / (float) (1 << 24);
  }

  public double nextDouble() {
    long l = ((long) (next(26)) << 27) + next(27);

    return l / (double) (1L << 53);
  }

  public char nextChar() {
    return (char) (next(16));
  }

  public short nextShort() {
    return (short) (next(16));
  }

  public byte nextByte() {
    return (byte) (next(8));
  }

  public double nextGaussian() {
    if (have_next_next_gaussian) {
      have_next_next_gaussian = false;
      return next_next_gaussian;
    } else {
      double v1;
      double v2;
      double s;

      do {
        v1 = 2 * nextDouble() - 1; // between -1 and 1
        v2 = 2 * nextDouble() - 1; // between -1 and 1
        s = v1 * v1 + v2 * v2;
      } while (s >= 1);
      double multiplier = Math.sqrt(-2 * Math.log(s) / s);

      next_next_gaussian = v2 * multiplier;
      have_next_next_gaussian = true;
      return v1 * multiplier;
    }
  }

  public boolean nextBooleanEfficiently() {
    boolean b;

    if (stored_bits_left-- == 0) {
      stored = next(32);
      stored_bits_left = 31;
    }
    b = (stored & 1) == 0;
    stored >>>= 1;
    return b;
  }

  public String returnName() {
    return "JUR";
  }
}
