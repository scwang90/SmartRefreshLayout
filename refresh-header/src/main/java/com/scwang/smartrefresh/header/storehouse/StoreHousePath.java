package com.scwang.smartrefresh.header.storehouse;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * StoreHouse 路径
 * Created by srain on 11/7/14.
 */
public class StoreHousePath {

    private static final SparseArray<float[]> sPointList;

    static {
        sPointList = new SparseArray<>();
        float[][] LETTERS = new float[][]{
                new float[]{
                        // A
                        24, 0, 1, 22,
                        1, 22, 1, 72,
                        24, 0, 47, 22,
                        47, 22, 47, 72,
                        1, 48, 47, 48
                },

                new float[]{
                        // B
                        0, 0, 0, 72,
                        0, 0, 37, 0,
                        37, 0, 47, 11,
                        47, 11, 47, 26,
                        47, 26, 38, 36,
                        38, 36, 0, 36,
                        38, 36, 47, 46,
                        47, 46, 47, 61,
                        47, 61, 38, 71,
                        37, 72, 0, 72,
                },

                new float[]{
                        // C
                        47, 0, 0, 0,
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                },

                new float[]{
                        // D
                        0, 0, 0, 72,
                        0, 0, 24, 0,
                        24, 0, 47, 22,
                        47, 22, 47, 48,
                        47, 48, 23, 72,
                        23, 72, 0, 72,
                },

                new float[]{
                        // E
                        0, 0, 0, 72,
                        0, 0, 47, 0,
                        0, 36, 37, 36,
                        0, 72, 47, 72,
                },

                new float[]{
                        // F
                        0, 0, 0, 72,
                        0, 0, 47, 0,
                        0, 36, 37, 36,
                },

                new float[]{
                        // G
                        47, 23, 47, 0,
                        47, 0, 0, 0,
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                        47, 72, 47, 48,
                        47, 48, 24, 48,
                },

                new float[]{
                        // H
                        0, 0, 0, 72,
                        0, 36, 47, 36,
                        47, 0, 47, 72,
                },

                new float[]{
                        // I
                        0, 0, 47, 0,
                        24, 0, 24, 72,
                        0, 72, 47, 72,
                },

                new float[]{
                        // J
                        47, 0, 47, 72,
                        47, 72, 24, 72,
                        24, 72, 0, 48,
                },

                new float[]{
                        // K
                        0, 0, 0, 72,
                        47, 0, 3, 33,
                        3, 38, 47, 72,
                },

                new float[]{
                        // L
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                },

                new float[]{
                        // M
                        0, 0, 0, 72,
                        0, 0, 24, 23,
                        24, 23, 47, 0,
                        47, 0, 47, 72,
                },

                new float[]{
                        // N
                        0, 0, 0, 72,
                        0, 0, 47, 72,
                        47, 72, 47, 0,
                },

                new float[]{
                        // O
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                        47, 72, 47, 0,
                        47, 0, 0, 0,
                },

                new float[]{
                        // P
                        0, 0, 0, 72,
                        0, 0, 47, 0,
                        47, 0, 47, 36,
                        47, 36, 0, 36,
                },

                new float[]{
                        // Q
                        0, 0, 0, 72,
                        0, 72, 23, 72,
                        23, 72, 47, 48,
                        47, 48, 47, 0,
                        47, 0, 0, 0,
                        24, 28, 47, 71,
                },

                new float[]{
                        // R
                        0, 0, 0, 72,
                        0, 0, 47, 0,
                        47, 0, 47, 36,
                        47, 36, 0, 36,
                        0, 37, 47, 72,
                },

                new float[]{
                        // S
                        47, 0, 0, 0,
                        0, 0, 0, 36,
                        0, 36, 47, 36,
                        47, 36, 47, 72,
                        47, 72, 0, 72,
                },

                new float[]{
                        // T
                        0, 0, 47, 0,
                        24, 0, 24, 72,
                },

                new float[]{
                        // U
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                        47, 72, 47, 0,
                },

                new float[]{
                        // V
                        0, 0, 24, 72,
                        24, 72, 47, 0,
                },

                new float[]{
                        // W
                        0, 0, 0, 72,
                        0, 72, 24, 49,
                        24, 49, 47, 72,
                        47, 72, 47, 0
                },

                new float[]{
                        // X
                        0, 0, 47, 72,
                        47, 0, 0, 72
                },

                new float[]{
                        // Y
                        0, 0, 24, 23,
                        47, 0, 24, 23,
                        24, 23, 24, 72
                },

                new float[]{
                        // Z
                        0, 0, 47, 0,
                        47, 0, 0, 72,
                        0, 72, 47, 72
                },
        };
        final float[][] NUMBERS = new float[][]{
                new float[]{
                        // 0
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                        47, 72, 47, 0,
                        47, 0, 0, 0,
                },
                new float[]{
                        // 1
                        24, 0, 24, 72,
                },

                new float[]{
                        // 2
                        0, 0, 47, 0,
                        47, 0, 47, 36,
                        47, 36, 0, 36,
                        0, 36, 0, 72,
                        0, 72, 47, 72
                },

                new float[]{
                        // 3
                        0, 0, 47, 0,
                        47, 0, 47, 36,
                        47, 36, 0, 36,
                        47, 36, 47, 72,
                        47, 72, 0, 72,
                },

                new float[]{
                        // 4
                        0, 0, 0, 36,
                        0, 36, 47, 36,
                        47, 0, 47, 72,
                },

                new float[]{
                        // 5
                        0, 0, 0, 36,
                        0, 36, 47, 36,
                        47, 36, 47, 72,
                        47, 72, 0, 72,
                        0, 0, 47, 0
                },

                new float[]{
                        // 6
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                        47, 72, 47, 36,
                        47, 36, 0, 36
                },

                new float[]{
                        // 7
                        0, 0, 47, 0,
                        47, 0, 47, 72
                },

                new float[]{
                        // 8
                        0, 0, 0, 72,
                        0, 72, 47, 72,
                        47, 72, 47, 0,
                        47, 0, 0, 0,
                        0, 36, 47, 36
                },

                new float[]{
                        // 9
                        47, 0, 0, 0,
                        0, 0, 0, 36,
                        0, 36, 47, 36,
                        47, 0, 47, 72,
                }
        };
        // A - Z
        for (int i = 0; i < LETTERS.length; i++) {
            sPointList.append(i + 65, LETTERS[i]);
        }
        // a - z
        for (int i = 0; i < LETTERS.length; i++) {
            sPointList.append(i + 65 + 32, LETTERS[i]);
        }
        // 0 - 9
        for (int i = 0; i < NUMBERS.length; i++) {
            sPointList.append(i + 48, NUMBERS[i]);
        }
        // blank
        sPointList.append(' ', new float[]{});
        // -
        sPointList.append('-', new float[]{
                0, 36, 47, 36
        });
        // .
        sPointList.append('.', new float[]{
                24, 60, 24, 72
        });
    }

//    public static ArrayList<float[]> getPath(String str) {
//        return getPath(str, 1, 14);
//    }

    /**
     * 根据符号和自提获取路径
     * @param str 字符串
     * @param scale 缩放
     * @param gapBetweenLetter 字符
     * @return ArrayList of float[] {x1, y1, x2, y2}
     */
    @SuppressWarnings("SameParameterValue")
    public static ArrayList<float[]> getPath(String str, float scale, int gapBetweenLetter) {
        ArrayList<float[]> list = new ArrayList<>();
        float offsetForWidth = 0;
        for (int i = 0; i < str.length(); i++) {
            int pos = str.charAt(i);
            int key = sPointList.indexOfKey(pos);
            if (key == -1) {
                continue;
            }
            float[] points = sPointList.get(pos);
            int pointCount = points.length / 4;

            for (int j = 0; j < pointCount; j++) {
                float[] line = new float[4];
                for (int k = 0; k < 4; k++) {
                    float l = points[j * 4 + k];
                    // x
                    if (k % 2 == 0) {
                        line[k] = (l + offsetForWidth) * scale;
                    }
                    // y
                    else {
                        line[k] = l * scale;
                    }
                }
                list.add(line);
            }
            offsetForWidth += 57 + gapBetweenLetter;
        }
        return list;
    }
}
