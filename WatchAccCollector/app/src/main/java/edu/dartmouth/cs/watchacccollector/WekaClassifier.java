package edu.dartmouth.cs.watchacccollector;

class WekaClassifier {

  public static double classify(Object[] i)
          throws Exception {

    double p = Double.NaN;
    p = WekaClassifier.N775cb4480(i);
    return p;
  }
  static double N775cb4480(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() <= 167.20233) {
      p = 0;
    } else if (((Double) i[0]).doubleValue() > 167.20233) {
      p = WekaClassifier.N674020301(i);
    }
    return p;
  }
  static double N674020301(Object []i) {
    double p = Double.NaN;
    if (i[7] == null) {
      p = 1;
    } else if (((Double) i[7]).doubleValue() <= 34.128321) {
      p = WekaClassifier.N230164452(i);
    } else if (((Double) i[7]).doubleValue() > 34.128321) {
      p = WekaClassifier.N4352a0f35(i);
    }
    return p;
  }
  static double N230164452(Object []i) {
    double p = Double.NaN;
    if (i[9] == null) {
      p = 1;
    } else if (((Double) i[9]).doubleValue() <= 22.77249) {
      p = WekaClassifier.N305dfff73(i);
    } else if (((Double) i[9]).doubleValue() > 22.77249) {
      p = 2;
    }
    return p;
  }
  static double N305dfff73(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 1;
    } else if (((Double) i[1]).doubleValue() <= 55.099878) {
      p = WekaClassifier.N6b4fc9ef4(i);
    } else if (((Double) i[1]).doubleValue() > 55.099878) {
      p = 1;
    }
    return p;
  }
  static double N6b4fc9ef4(Object []i) {
    double p = Double.NaN;
    if (i[4] == null) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() <= 38.803824) {
      p = 1;
    } else if (((Double) i[4]).doubleValue() > 38.803824) {
      p = 2;
    }
    return p;
  }
  static double N4352a0f35(Object []i) {
    double p = Double.NaN;
    if (i[0] == null) {
      p = 2;
    } else if (((Double) i[0]).doubleValue() <= 853.430079) {
      p = WekaClassifier.N65c4d40c6(i);
    } else if (((Double) i[0]).doubleValue() > 853.430079) {
      p = 2;
    }
    return p;
  }
  static double N65c4d40c6(Object []i) {
    double p = Double.NaN;
    if (i[1] == null) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() <= 178.29519) {
      p = 2;
    } else if (((Double) i[1]).doubleValue() > 178.29519) {
      p = 1;
    }
    return p;
  }
}
