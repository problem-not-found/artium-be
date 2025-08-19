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


  public static List<float[]> encodePreferences(List<? extends Enum<?>> preferences) {
    List<float[]> vectors = new java.util.ArrayList<>();
    if (preferences == null || preferences.isEmpty()) {
      return vectors;
    }

    for (Enum<?> pref : preferences) {
      vectors.add(encode(pref));
    }
    return vectors;
  }

  private static float[] encode(Enum<?> pref) {
    if (pref == null) {
      return new float[0];
    }
    String className = pref.getClass().getSimpleName();
    switch (className) {
      case "ThemePreference" -> {
        return encodeEnum(pref, com.likelion13.artium.domain.user.entity.ThemePreference.values().length);
      }
      case "MoodPreference" -> {
        return encodeEnum(pref, com.likelion13.artium.domain.user.entity.MoodPreference.values().length);
      }
      case "FormatPreference" -> {
        return encodeEnum(pref, com.likelion13.artium.domain.user.entity.FormatPreference.values().length);
      }
      default -> {
        return new float[0];
      }
    }
  }

  private static float[] encodeEnum(Enum<?> pref, int size) {
    float[] vec = new float[size];
    vec[pref.ordinal()] = 1.0f;
    return vec;
  }
}
