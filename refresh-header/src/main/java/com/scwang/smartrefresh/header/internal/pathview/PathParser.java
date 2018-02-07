/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.scwang.smartrefresh.header.internal.pathview;

import android.graphics.Matrix;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// This class is a duplicate from the PathParser.java of frameworks/base, with slight
// update on incompatible API like copyOfRange().
@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
class PathParser {
    private static final String LOGTAG = "PathParser";

    // Copy from Arrays.copyOfRange() which is only available from API level 9.

    /**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
     * end (exclusive). The original order of elements is preserved.
     * If {@code end} is greater than {@code original.length}, the result is padded
     * with the value {@code 0.0f}.
     *
     * @param original the original array
     * @param start    the start index, inclusive
     * @param end      the end index, exclusive
     * @return the new array
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0 || start > original.length}
     * @throws IllegalArgumentException       if {@code start > end}
     * @throws NullPointerException           if {@code original == null}
     */
    static float[] copyOfRange(float[] original, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        float[] result = new float[resultLength];
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

    public static List<Path> transformScale(float ratioWidth, float ratioHeight, List<Path> orginPaths, List<String> orginSvgs) {
        Matrix matrix = new Matrix();
        matrix.setScale(ratioWidth, ratioHeight);
        List<Path> paths = new ArrayList<>();
        if (Build.VERSION.SDK_INT > 16) {
            for (Path path : orginPaths) {
                Path nPath = new Path();
                path.transform(matrix, nPath);
                paths.add(nPath);
            }
        } else {
            for (String svgPath : orginSvgs) {
                Path path = new Path();
                PathDataNode[] nodes = createNodesFromPathData(svgPath);
                transformScaleNodes(ratioWidth, ratioHeight, nodes);
                PathDataNode.nodesToPath(nodes, path);
                paths.add(path);
            }

        }
        return paths;
    }

    private static void transformScaleNodes(float ratioWidth, float ratioHeight, PathDataNode[] node) {
        for (PathDataNode aNode : node) {
            transformScaleCommand(ratioWidth, ratioHeight, aNode.type, aNode.params);
        }
    }

    private static void transformScaleCommand(float ratioWidth, float ratioHeight, char cmd, float[] val) {
        int incr = 2;
        switch (cmd) {
            case 'z':
            case 'Z':
                break;
            case 'm':
            case 'M':
            case 'l':
            case 'L':
            case 't':
            case 'T':
                incr = 2;
                break;
            case 'h':
            case 'H':
            case 'v':
            case 'V':
                incr = 1;
                break;
            case 'c':
            case 'C':
                incr = 6;
                break;
            case 's':
            case 'S':
            case 'q':
            case 'Q':
                incr = 4;
                break;
            case 'a':
            case 'A':
                incr = 7;
                break;
        }
        for (int k = 0; k < val.length; k += incr) {
            switch (cmd) {
                case 'm': // moveto - Start a new sub-path (relative)
                case 'M': // moveto - Start a new sub-path
                case 'l': // lineto - Draw a line from the current point (relative)
                case 'L': // lineto - Draw a line from the current point
                case 't': // Draws a quadratic Bézier curve(reflective control point)(relative)
                case 'T': // Draws a quadratic Bézier curve (reflective control point)
                    val[k] *= ratioWidth;
                    val[k + 1] *= ratioHeight;
                    break;
                case 'h': // horizontal lineto - Draws a horizontal line (relative)
                case 'H': // horizontal lineto - Draws a horizontal line
                    val[k] *= ratioWidth;
                    break;
                case 'v': // vertical lineto - Draws a vertical line from the current point (r)
                case 'V': // vertical lineto - Draws a vertical line from the current point
                    val[k] *= ratioHeight;
                    break;
                case 'c': // curveto - Draws a cubic Bézier curve (relative)
                case 'C': // curveto - Draws a cubic Bézier curve
                    val[k] *= ratioWidth;
                    val[k + 1] *= ratioHeight;
                    val[k + 2] *= ratioWidth;
                    val[k + 3] *= ratioHeight;
                    val[k + 4] *= ratioWidth;
                    val[k + 5] *= ratioHeight;
                    break;
                case 's': // smooth curveto - Draws a cubic Bézier curve (reflective cp)
                case 'S': // shorthand/smooth curveto Draws a cubic Bézier curve(reflective cp)
                case 'q': // Draws a quadratic Bézier (relative)
                case 'Q': // Draws a quadratic Bézier
                    val[k] *= ratioWidth;
                    val[k + 1] *= ratioHeight;
                    val[k + 2] *= ratioWidth;
                    val[k + 3] *= ratioHeight;
                case 'a': // Draws an elliptical arc
                case 'A': // Draws an elliptical arc
                    val[k] *= ratioWidth;
                    val[k + 1] *= ratioHeight;
                    val[k + 5] *= ratioWidth;
                    val[k + 6] *= ratioHeight;
                    break;
            }
        }
    }

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return the generated Path object.
     */
    public static Path createPathFromPathData(String pathData) {
        Path path = new Path();
        PathDataNode[] nodes = createNodesFromPathData(pathData);
        if (nodes != null) {
            try {
                PathDataNode.nodesToPath(nodes, path);
            } catch (RuntimeException e) {
                throw new RuntimeException("Error in parsing " + pathData, e);
            }
            return path;
        }
        return null;
    }

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return an array of the PathDataNode.
     */
    public static PathDataNode[] createNodesFromPathData(String pathData) {
        if (pathData == null) {
            return null;
        }
        int start = 0;
        int end = 1;

        ArrayList<PathDataNode> list = new ArrayList<>();
        while (end < pathData.length()) {
            end = nextStart(pathData, end);
            String s = pathData.substring(start, end).trim();
            if (s.length() > 0) {
                float[] val = getFloats(s);
                addNode(list, s.charAt(0), val);
            }

            start = end;
            end++;
        }
        if ((end - start) == 1 && start < pathData.length()) {
            addNode(list, pathData.charAt(start), new float[0]);
        }
        return list.toArray(new PathDataNode[list.size()]);
    }

//    /**
//     * @param source The array of PathDataNode to be duplicated.
//     * @return a deep copy of the <code>source</code>.
//     */
//    public static PathDataNode[] deepCopyNodes(PathDataNode[] source) {
//        if (source == null) {
//            return null;
//        }
//        PathDataNode[] copy = new PathParser.PathDataNode[source.length];
//        for (int i = 0; i < source.length; i++) {
//            copy[i] = new PathDataNode(source[i]);
//        }
//        return copy;
//    }

//    /**
//     * @param nodesFrom The source path represented in an array of PathDataNode
//     * @param nodesTo   The target path represented in an array of PathDataNode
//     * @return whether the <code>nodesFrom</code> can morph into <code>nodesTo</code>
//     */
//    public static boolean canMorph(PathDataNode[] nodesFrom, PathDataNode[] nodesTo) {
//        if (nodesFrom == null || nodesTo == null) {
//            return false;
//        }
//
//        if (nodesFrom.length != nodesTo.length) {
//            return false;
//        }
//
//        for (int i = 0; i < nodesFrom.length; i++) {
//            if (nodesFrom[i].type != nodesTo[i].type
//                    || nodesFrom[i].params.length != nodesTo[i].params.length) {
//                return false;
//            }
//        }
//        return true;
//    }

//    /**
//     * Update the target's data to match the source.
//     * Before calling this, make sure canMorph(target, source) is true.
//     *
//     * @param target The target path represented in an array of PathDataNode
//     * @param source The source path represented in an array of PathDataNode
//     */
//    public static void updateNodes(PathDataNode[] target, PathDataNode[] source) {
//        for (int i = 0; i < source.length; i++) {
//            target[i].type = source[i].type;
//            for (int j = 0; j < source[i].params.length; j++) {
//                target[i].params[j] = source[i].params[j];
//            }
//        }
//    }

    private static int nextStart(String s, int end) {
        char c;

        while (end < s.length()) {
            c = s.charAt(end);
            // Note that 'e' or 'E' are not valid path commands, but could be
            // used for floating point numbers' scientific notation.
            // Therefore, when searching for next command, we should ignore 'e'
            // and 'E'.
            if ((((c - 'A') * (c - 'Z') <= 0) || ((c - 'a') * (c - 'z') <= 0))
                    && c != 'e' && c != 'E') {
                return end;
            }
            end++;
        }
        return end;
    }

    private static void addNode(ArrayList<PathDataNode> list, char cmd, float[] val) {
        list.add(new PathDataNode(cmd, val));
    }

    private static class ExtractFloatResult {
        // We need to return the position of the next separator and whether the
        // next float starts with a '-' or a '.'.
        int mEndPosition;
        boolean mEndWithNegOrDot;
    }

    /**
     * Parse the floats in the string.
     * This is an optimized version of parseFloat(s.split(",|\\s"));
     *
     * @param s the string containing a command and list of floats
     * @return array of floats
     */
    private static float[] getFloats(String s) {
        if (s.charAt(0) == 'z' | s.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            float[] results = new float[s.length()];
            int count = 0;
            int startPosition = 1;
            int endPosition;

            ExtractFloatResult result = new ExtractFloatResult();
            int totalLength = s.length();

            // The startPosition should always be the first character of the
            // current number, and endPosition is the character after the current
            // number.
            while (startPosition < totalLength) {
                extract(s, startPosition, result);
                endPosition = result.mEndPosition;

                if (startPosition < endPosition) {
                    results[count++] = Float.parseFloat(
                            s.substring(startPosition, endPosition));
                }

                if (result.mEndWithNegOrDot) {
                    // Keep the '-' or '.' sign with next number.
                    startPosition = endPosition;
                } else {
                    startPosition = endPosition + 1;
                }
            }
            return copyOfRange(results, 0, count);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + s + "\"", e);
        }
    }

    /**
     * Calculate the position of the next comma or space or negative sign
     *
     * @param s      the string to search
     * @param start  the position to start searching
     * @param result the result of the extraction, including the position of the
     *               the starting position of next number, whether it is ending with a '-'.
     */
    private static void extract(String s, int start, ExtractFloatResult result) {
        // Now looking for ' ', ',', '.' or '-' from the start.
        int currentIndex = start;
        boolean foundSeparator = false;
        result.mEndWithNegOrDot = false;
        boolean secondDot = false;
        boolean isExponential = false;
        for (; currentIndex < s.length(); currentIndex++) {
            boolean isPrevExponential = isExponential;
            isExponential = false;
            char currentChar = s.charAt(currentIndex);
            switch (currentChar) {
                case ' ':
                case ',':
                    foundSeparator = true;
                    break;
                case '-':
                    // The negative sign following a 'e' or 'E' is not a separator.
                    if (currentIndex != start && !isPrevExponential) {
                        foundSeparator = true;
                        result.mEndWithNegOrDot = true;
                    }
                    break;
                case '.':
                    if (!secondDot) {
                        secondDot = true;
                    } else {
                        // This is the second dot, and it is considered as a separator.
                        foundSeparator = true;
                        result.mEndWithNegOrDot = true;
                    }
                    break;
                case 'e':
                case 'E':
                    isExponential = true;
                    break;
            }
            if (foundSeparator) {
                break;
            }
        }
        // When there is nothing found, then we put the end position to the end
        // of the string.
        result.mEndPosition = currentIndex;
    }

    /**
     * Each PathDataNode represents one command in the "d" attribute of the svg
     * file.
     * An array of PathDataNode can represent the whole "d" attribute.
     */
    @SuppressWarnings({"PointlessArithmeticExpression", "WeakerAccess"})
    public static class PathDataNode {
        /*package*/
        char type;
        float[] params;

        PathDataNode(char type, float[] params) {
            this.type = type;
            this.params = params;
        }

//        PathDataNode(PathDataNode n) {
//            type = n.type;
//            params = copyOfRange(n.params, 0, n.params.length);
//        }

        /**
         * Convert an array of PathDataNode to Path.
         *
         * @param node The source array of PathDataNode.
         * @param path The target Path object.
         */
        public static void nodesToPath(PathDataNode[] node, Path path) {
            float[] current = new float[6];
            char previousCommand = 'm';
            for (PathDataNode aNode : node) {
                addCommand(path, current, previousCommand, aNode.type, aNode.params);
                previousCommand = aNode.type;
            }
        }

//        /**
//         * The current PathDataNode will be interpolated between the
//         * <code>nodeFrom</code> and <code>nodeTo</code> according to the
//         * <code>fraction</code>.
//         *
//         * @param nodeFrom The start value as a PathDataNode.
//         * @param nodeTo   The end value as a PathDataNode
//         * @param fraction The fraction to interpolate.
//         */
//        public void interpolatePathDataNode(PathDataNode nodeFrom,
//                                            PathDataNode nodeTo, float fraction) {
//            for (int i = 0; i < nodeFrom.params.length; i++) {
//                params[i] = nodeFrom.params[i] * (1 - fraction)
//                        + nodeTo.params[i] * fraction;
//            }
//        }

        private static void addCommand(Path path, float[] current,
                                       char previousCmd, char cmd, float[] val) {

            int incr = 2;
            float currentX = current[0];
            float currentY = current[1];
            float ctrlPointX = current[2];
            float ctrlPointY = current[3];
            float currentSegmentStartX = current[4];
            float currentSegmentStartY = current[5];
            float reflectiveCtrlPointX;
            float reflectiveCtrlPointY;

            switch (cmd) {
                case 'z':
                case 'Z':
                    path.close();
                    // Path is closed here, but we need to move the pen to the
                    // closed position. So we cache the segment's starting position,
                    // and restore it here.
                    currentX = currentSegmentStartX;
                    currentY = currentSegmentStartY;
                    ctrlPointX = currentSegmentStartX;
                    ctrlPointY = currentSegmentStartY;
                    path.moveTo(currentX, currentY);
                    break;
                case 'm':
                case 'M':
                case 'l':
                case 'L':
                case 't':
                case 'T':
                    incr = 2;
                    break;
                case 'h':
                case 'H':
                case 'v':
                case 'V':
                    incr = 1;
                    break;
                case 'c':
                case 'C':
                    incr = 6;
                    break;
                case 's':
                case 'S':
                case 'q':
                case 'Q':
                    incr = 4;
                    break;
                case 'a':
                case 'A':
                    incr = 7;
                    break;
            }

            for (int k = 0; k < val.length; k += incr) {
                switch (cmd) {
                    case 'm': // moveto - Start a new sub-path (relative)
                        currentX += val[k + 0];
                        currentY += val[k + 1];
                        if (k > 0) {
                            // According to the spec, if a moveto is followed by multiple
                            // pairs of coordinates, the subsequent pairs are treated as
                            // implicit lineto commands.
                            path.rLineTo(val[k + 0], val[k + 1]);
                        } else {
                            path.rMoveTo(val[k + 0], val[k + 1]);
                            currentSegmentStartX = currentX;
                            currentSegmentStartY = currentY;
                        }
                        break;
                    case 'M': // moveto - Start a new sub-path
                        currentX = val[k + 0];
                        currentY = val[k + 1];
                        if (k > 0) {
                            // According to the spec, if a moveto is followed by multiple
                            // pairs of coordinates, the subsequent pairs are treated as
                            // implicit lineto commands.
                            path.lineTo(val[k + 0], val[k + 1]);
                        } else {
                            path.moveTo(val[k + 0], val[k + 1]);
                            currentSegmentStartX = currentX;
                            currentSegmentStartY = currentY;
                        }
                        break;
                    case 'l': // lineto - Draw a line from the current point (relative)
                        path.rLineTo(val[k + 0], val[k + 1]);
                        currentX += val[k + 0];
                        currentY += val[k + 1];
                        break;
                    case 'L': // lineto - Draw a line from the current point
                        path.lineTo(val[k + 0], val[k + 1]);
                        currentX = val[k + 0];
                        currentY = val[k + 1];
                        break;
                    case 'h': // horizontal lineto - Draws a horizontal line (relative)
                        path.rLineTo(val[k + 0], 0);
                        currentX += val[k + 0];
                        break;
                    case 'H': // horizontal lineto - Draws a horizontal line
                        path.lineTo(val[k + 0], currentY);
                        currentX = val[k + 0];
                        break;
                    case 'v': // vertical lineto - Draws a vertical line from the current point (r)
                        path.rLineTo(0, val[k + 0]);
                        currentY += val[k + 0];
                        break;
                    case 'V': // vertical lineto - Draws a vertical line from the current point
                        path.lineTo(currentX, val[k + 0]);
                        currentY = val[k + 0];
                        break;
                    case 'c': // curveto - Draws a cubic Bézier curve (relative)
                        path.rCubicTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3],
                                val[k + 4], val[k + 5]);

                        ctrlPointX = currentX + val[k + 2];
                        ctrlPointY = currentY + val[k + 3];
                        currentX += val[k + 4];
                        currentY += val[k + 5];

                        break;
                    case 'C': // curveto - Draws a cubic Bézier curve
                        path.cubicTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3],
                                val[k + 4], val[k + 5]);
                        currentX = val[k + 4];
                        currentY = val[k + 5];
                        ctrlPointX = val[k + 2];
                        ctrlPointY = val[k + 3];
                        break;
                    case 's': // smooth curveto - Draws a cubic Bézier curve (reflective cp)
                        reflectiveCtrlPointX = 0;
                        reflectiveCtrlPointY = 0;
                        if (previousCmd == 'c' || previousCmd == 's'
                                || previousCmd == 'C' || previousCmd == 'S') {
                            reflectiveCtrlPointX = currentX - ctrlPointX;
                            reflectiveCtrlPointY = currentY - ctrlPointY;
                        }
                        path.rCubicTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                val[k + 0], val[k + 1],
                                val[k + 2], val[k + 3]);

                        ctrlPointX = currentX + val[k + 0];
                        ctrlPointY = currentY + val[k + 1];
                        currentX += val[k + 2];
                        currentY += val[k + 3];
                        break;
                    case 'S': // shorthand/smooth curveto Draws a cubic Bézier curve(reflective cp)
                        reflectiveCtrlPointX = currentX;
                        reflectiveCtrlPointY = currentY;
                        if (previousCmd == 'c' || previousCmd == 's'
                                || previousCmd == 'C' || previousCmd == 'S') {
                            reflectiveCtrlPointX = 2 * currentX - ctrlPointX;
                            reflectiveCtrlPointY = 2 * currentY - ctrlPointY;
                        }
                        path.cubicTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                val[k + 0], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = val[k + 0];
                        ctrlPointY = val[k + 1];
                        currentX = val[k + 2];
                        currentY = val[k + 3];
                        break;
                    case 'q': // Draws a quadratic Bézier (relative)
                        path.rQuadTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = currentX + val[k + 0];
                        ctrlPointY = currentY + val[k + 1];
                        currentX += val[k + 2];
                        currentY += val[k + 3];
                        break;
                    case 'Q': // Draws a quadratic Bézier
                        path.quadTo(val[k + 0], val[k + 1], val[k + 2], val[k + 3]);
                        ctrlPointX = val[k + 0];
                        ctrlPointY = val[k + 1];
                        currentX = val[k + 2];
                        currentY = val[k + 3];
                        break;
                    case 't': // Draws a quadratic Bézier curve(reflective control point)(relative)
                        reflectiveCtrlPointX = 0;
                        reflectiveCtrlPointY = 0;
                        if (previousCmd == 'q' || previousCmd == 't'
                                || previousCmd == 'Q' || previousCmd == 'T') {
                            reflectiveCtrlPointX = currentX - ctrlPointX;
                            reflectiveCtrlPointY = currentY - ctrlPointY;
                        }
                        path.rQuadTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                val[k + 0], val[k + 1]);
                        ctrlPointX = currentX + reflectiveCtrlPointX;
                        ctrlPointY = currentY + reflectiveCtrlPointY;
                        currentX += val[k + 0];
                        currentY += val[k + 1];
                        break;
                    case 'T': // Draws a quadratic Bézier curve (reflective control point)
                        reflectiveCtrlPointX = currentX;
                        reflectiveCtrlPointY = currentY;
                        if (previousCmd == 'q' || previousCmd == 't'
                                || previousCmd == 'Q' || previousCmd == 'T') {
                            reflectiveCtrlPointX = 2 * currentX - ctrlPointX;
                            reflectiveCtrlPointY = 2 * currentY - ctrlPointY;
                        }
                        path.quadTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                val[k + 0], val[k + 1]);
                        ctrlPointX = reflectiveCtrlPointX;
                        ctrlPointY = reflectiveCtrlPointY;
                        currentX = val[k + 0];
                        currentY = val[k + 1];
                        break;
                    case 'a': // Draws an elliptical arc
                        // (rx ry x-axis-rotation large-arc-flag sweep-flag x y)
                        drawArc(path,
                                currentX,
                                currentY,
                                val[k + 5] + currentX,
                                val[k + 6] + currentY,
                                val[k + 0],
                                val[k + 1],
                                val[k + 2],
                                val[k + 3] != 0,
                                val[k + 4] != 0);
                        currentX += val[k + 5];
                        currentY += val[k + 6];
                        ctrlPointX = currentX;
                        ctrlPointY = currentY;
                        break;
                    case 'A': // Draws an elliptical arc
                        drawArc(path,
                                currentX,
                                currentY,
                                val[k + 5],
                                val[k + 6],
                                val[k + 0],
                                val[k + 1],
                                val[k + 2],
                                val[k + 3] != 0,
                                val[k + 4] != 0);
                        currentX = val[k + 5];
                        currentY = val[k + 6];
                        ctrlPointX = currentX;
                        ctrlPointY = currentY;
                        break;
                }
                previousCmd = cmd;
            }
            current[0] = currentX;
            current[1] = currentY;
            current[2] = ctrlPointX;
            current[3] = ctrlPointY;
            current[4] = currentSegmentStartX;
            current[5] = currentSegmentStartY;
        }

        private static void drawArc(Path p,
                                    float x0,
                                    float y0,
                                    float x1,
                                    float y1,
                                    float a,
                                    float b,
                                    float theta,
                                    boolean isMoreThanHalf,
                                    boolean isPositiveArc) {

            /* Convert rotation angle from degrees to radians */
            double thetaD = Math.toRadians(theta);
            /* Pre-compute rotation matrix entries */
            double cosTheta = Math.cos(thetaD);
            double sinTheta = Math.sin(thetaD);
            /* Transform (x0, y0) and (x1, y1) into unit space */
            /* using (inverse) rotation, followed by (inverse) scale */
            double x0p = (x0 * cosTheta + y0 * sinTheta) / a;
            double y0p = (-x0 * sinTheta + y0 * cosTheta) / b;
            double x1p = (x1 * cosTheta + y1 * sinTheta) / a;
            double y1p = (-x1 * sinTheta + y1 * cosTheta) / b;

            /* Compute differences and averages */
            double dx = x0p - x1p;
            double dy = y0p - y1p;
            double xm = (x0p + x1p) / 2;
            double ym = (y0p + y1p) / 2;
            /* Solve for intersecting unit circles */
            double dsq = dx * dx + dy * dy;
            if (dsq == 0.0) {
                Log.w(LOGTAG, " Points are coincident");
                return; /* Points are coincident */
            }
            double disc = 1.0 / dsq - 1.0 / 4.0;
            if (disc < 0.0) {
                Log.w(LOGTAG, "Points are too far apart " + dsq);
                float adjust = (float) (Math.sqrt(dsq) / 1.99999);
                drawArc(p, x0, y0, x1, y1, a * adjust,
                        b * adjust, theta, isMoreThanHalf, isPositiveArc);
                return; /* Points are too far apart */
            }
            double s = Math.sqrt(disc);
            double sdx = s * dx;
            double sdy = s * dy;
            double cx;
            double cy;
            if (isMoreThanHalf == isPositiveArc) {
                cx = xm - sdy;
                cy = ym + sdx;
            } else {
                cx = xm + sdy;
                cy = ym - sdx;
            }

            double eta0 = Math.atan2((y0p - cy), (x0p - cx));

            double eta1 = Math.atan2((y1p - cy), (x1p - cx));

            double sweep = (eta1 - eta0);
            if (isPositiveArc != (sweep >= 0)) {
                if (sweep > 0) {
                    sweep -= 2 * Math.PI;
                } else {
                    sweep += 2 * Math.PI;
                }
            }

            cx *= a;
            cy *= b;
            double tcx = cx;
            cx = cx * cosTheta - cy * sinTheta;
            cy = tcx * sinTheta + cy * cosTheta;

            arcToBezier(p, cx, cy, a, b, x0, y0, thetaD, eta0, sweep);
        }

        /**
         * Converts an arc to cubic Bezier segments and records them in p.
         *
         * @param p     The target for the cubic Bezier segments
         * @param cx    The x coordinate center of the ellipse
         * @param cy    The y coordinate center of the ellipse
         * @param a     The radius of the ellipse in the horizontal direction
         * @param b     The radius of the ellipse in the vertical direction
         * @param e1x   E(eta1) x coordinate of the starting point of the arc
         * @param e1y   E(eta2) y coordinate of the starting point of the arc
         * @param theta The angle that the ellipse bounding rectangle makes with horizontal plane
         * @param start The start angle of the arc on the ellipse
         * @param sweep The angle (positive or negative) of the sweep of the arc on the ellipse
         */
        private static void arcToBezier(Path p,
                                        double cx,
                                        double cy,
                                        double a,
                                        double b,
                                        double e1x,
                                        double e1y,
                                        double theta,
                                        double start,
                                        double sweep) {
            // Taken from equations at: http://spaceroots.org/documents/ellipse/node8.html
            // and http://www.spaceroots.org/documents/ellipse/node22.html

            // Maximum of 45 degrees per cubic Bezier segment
            int numSegments = (int) Math.ceil(Math.abs(sweep * 4 / Math.PI));

            double eta1 = start;
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            double cosEta1 = Math.cos(eta1);
            double sinEta1 = Math.sin(eta1);
            double ep1x = (-a * cosTheta * sinEta1) - (b * sinTheta * cosEta1);
            double ep1y = (-a * sinTheta * sinEta1) + (b * cosTheta * cosEta1);

            double anglePerSegment = sweep / numSegments;
            for (int i = 0; i < numSegments; i++) {
                double eta2 = eta1 + anglePerSegment;
                double sinEta2 = Math.sin(eta2);
                double cosEta2 = Math.cos(eta2);
                double e2x = cx + (a * cosTheta * cosEta2) - (b * sinTheta * sinEta2);
                double e2y = cy + (a * sinTheta * cosEta2) + (b * cosTheta * sinEta2);
                double ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2;
                double ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2;
                double tanDiff2 = Math.tan((eta2 - eta1) / 2);
                double alpha =
                        Math.sin(eta2 - eta1) * (Math.sqrt(4 + (3 * tanDiff2 * tanDiff2)) - 1) / 3;
                double q1x = e1x + alpha * ep1x;
                double q1y = e1y + alpha * ep1y;
                double q2x = e2x - alpha * ep2x;
                double q2y = e2y - alpha * ep2y;

                // Adding this no-op call to workaround a proguard related issue.
                p.rLineTo(0, 0);

                p.cubicTo((float) q1x,
                        (float) q1y,
                        (float) q2x,
                        (float) q2y,
                        (float) e2x,
                        (float) e2y);
                eta1 = eta2;
                e1x = e2x;
                e1y = e2y;
                ep1x = ep2x;
                ep1y = ep2y;
            }
        }
    }
}
