package org.fonteditor.hinter;

public class HintingCues {
  private int top_letter_o;
  private int bottom_letter_o;
  private int top_letter_upper_h;

  public HintingCues(int top_letter_o, int bottom_letter_o, int top_letter_upper_h) {
    this.top_letter_o = top_letter_o;
    this.bottom_letter_o = bottom_letter_o;
    this.top_letter_upper_h = top_letter_upper_h;
  }
   
  void setBottomLetterO(int bottom_letter_o) {
    this.bottom_letter_o = bottom_letter_o;
  }

  public int getBottomLetterO() {
    return bottom_letter_o;
  }
  
  void setTopLetterO(int top_letter_o) {
    this.top_letter_o = top_letter_o;
  }

  public int getTopLetterO() {
    return top_letter_o;
  }
  
  void setTopLetterH(int top_letter_upper_h) {
    this.top_letter_upper_h = top_letter_upper_h;
  }

  public int getTopLetterH() {
    return top_letter_upper_h;
  }
  
    public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof HintingCues)) {
      return false;
    }

    HintingCues h_c = (HintingCues) o;

    if (h_c.hashCode() != hashCode()) {
      return false;
    }
    
    if (h_c.bottom_letter_o != bottom_letter_o) {
      return false;
    }
    
    if (h_c.top_letter_o != top_letter_o) {
      return false;
    }

    if (h_c.top_letter_upper_h != top_letter_upper_h) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int hash_code = 0;
    hash_code ^= top_letter_o + top_letter_o << 21;
    hash_code ^= bottom_letter_o + bottom_letter_o << 14;
    hash_code ^= top_letter_upper_h + top_letter_upper_h << 7;
    
    return hash_code;
  }
}
