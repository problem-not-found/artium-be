package com.likelion13.artium.global.ai.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl {
    private final EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        EmbeddingResponse resp = embeddingModel.embedForResponse(List.of(text));
        return resp.getResults().get(0).getOutput();
    }

    public static float[] mean(List<float[]> vecs) {
        if (vecs == null || vecs.isEmpty()) return null;
        int d = vecs.get(0).length;
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
        if (norm == 0) return v;
        float[] out = new float[v.length];
        for (int i = 0; i < v.length; i++) out[i] = (float) (v[i] / norm);
        return out;
    }

}
