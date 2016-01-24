package edu.dartmouth.cs.watchacccollector;

class WekaClassifier {

  public static double classify(Object[] i)
    throws Exception {

    double p = Double.NaN;
    p = WekaClassifier.N7b9a290(i);
    return p;
  }
  static double N7b9a290(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 86.26407) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() > 86.26407) {
    p = WekaClassifier.N32c5f9fe1(i);
    } 
    return p;
  }
  static double N32c5f9fe1(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 502.405914) {
    p = WekaClassifier.N639facbc2(i);
    } else if (((Double) i[0]).doubleValue() > 502.405914) {
      p = 2;
    } 
    return p;
  }
  static double N639facbc2(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 1;
    } else if (((Double) i[0]).doubleValue() <= 141.377476) {
    p = WekaClassifier.N8059dbd3(i);
    } else if (((Double) i[0]).doubleValue() > 141.377476) {
    p = WekaClassifier.N3ae86a5f6(i);
    } 
    return p;
  }
  static double N8059dbd3(Object []i) {
    double p = Double.NaN;
    if (i[22] == null) {
      p = 1;
    } else if (((Double) i[22]).doubleValue() <= 1.131086) {
    p = WekaClassifier.N28b6e7684(i);
    } else if (((Double) i[22]).doubleValue() > 1.131086) {
      p = 1;
    } 
    return p;
  }
  static double N28b6e7684(Object []i) {
    double p = Double.NaN;
    if (i[21] == null) {
      p = 1;
    } else if (((Double) i[21]).doubleValue() <= 0.568825) {
      p = 1;
    } else if (((Double) i[21]).doubleValue() > 0.568825) {
    p = WekaClassifier.N1271ba5(i);
    } 
    return p;
  }
  static double N1271ba5(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 4.809262) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() > 4.809262) {
      p = 2;
    } 
    return p;
  }
  static double N3ae86a5f6(Object []i) {
    double p = Double.NaN;
    if (i[29] == null) {
      p = 1;
    } else if (((Double) i[29]).doubleValue() <= 3.241519) {
      p = 1;
    } else if (((Double) i[29]).doubleValue() > 3.241519) {
    p = WekaClassifier.N709fa12f7(i);
    } 
    return p;
  }
  static double N709fa12f7(Object []i) {
    double p = Double.NaN;
    if (i[29] == null) {
      p = 1;
    } else if (((Double) i[29]).doubleValue() <= 4.325775) {
    p = WekaClassifier.Nf4e6d8(i);
    } else if (((Double) i[29]).doubleValue() > 4.325775) {
      p = 1;
    } 
    return p;
  }
  static double Nf4e6d8(Object []i) {
    double p = Double.NaN;
    if (i[5] == null) {
      p = 1;
    } else if (((Double) i[5]).doubleValue() <= 24.091962) {
    p = WekaClassifier.N6ce5d6229(i);
    } else if (((Double) i[5]).doubleValue() > 24.091962) {
      p = 2;
    } 
    return p;
  }
  static double N6ce5d6229(Object []i) {
    double p = Double.NaN;
    if (i[14] == null) {
      p = 1;
    } else if (((Double) i[14]).doubleValue() <= 6.675007) {
      p = 1;
    } else if (((Double) i[14]).doubleValue() > 6.675007) {
      p = 2;
    } 
    return p;
  }
}

