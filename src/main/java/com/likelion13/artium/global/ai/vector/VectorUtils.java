/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.ai.vector;

import java.util.List;

public class VectorUtils {

  public static float[] mean(List<float[]> vecs) {
    if (vecs == null || vecs.isEmpty()) return null;
    int d = -1;
    for (float[] vec : vecs) {
      if (vec != null) {
        d = vec.length;
        break;
      }
    }
    if (d <= 0) return null;
    float[] sum = new float[d];
    int n = 0;
    for (float[] v : vecs) {
      if (v == null || v.length != d) continue;
      for (int i = 0; i < d; i++) sum[i] += v[i];
      n++;
    }
    if (n == 0) return null;
    for (int i = 0; i < d; i++) sum[i] /= n;
    return sum;
  }

  public static float[] normalize(float[] v) {
    if (v == null) return null;
    double norm = 0;
    for (float x : v) norm += x * x;
    norm = Math.sqrt(norm);
    if (norm == 0) return v.clone();
    float[] out = new float[v.length];
    for (int i = 0; i < v.length; i++) out[i] = (float) (v[i] / norm);
    return out;
  }

  public static float[] scale(float[] v, double w) {
    float[] out = new float[v.length];
    for (int i = 0; i < v.length; i++) out[i] = (float) (v[i] * w);
    return out;
  }

  public static float[] addScaled(float[] a, float[] b, double wa, double wb) {
    float[] out = new float[a.length];
    for (int i = 0; i < a.length; i++) out[i] = (float) (a[i] * wa + b[i] * wb);
    return out;
  }
}
