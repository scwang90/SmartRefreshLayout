package com.scwang.smartrefreshlayout.internal.pathview;

import android.graphics.Matrix;
import android.graphics.Path;
import android.util.Log;

import java.util.Locale;

import static android.content.ContentValues.TAG;

class TextScanner {

    protected String input;
    protected int position = 0;
    protected int inputLength = 0;

    private NumberParser numberParser = new NumberParser();


    public TextScanner(String input) {
        this.input = input.trim();
        this.inputLength = this.input.length();
    }

    /**
     * Returns true if we have reached the end of the input.
     */
    public boolean empty() {
        return (position == inputLength);
    }

    protected boolean isWhitespace(int c) {
        return (c == ' ' || c == '\n' || c == '\r' || c == '\t');
    }

    public void skipWhitespace() {
        while (position < inputLength) {
            if (!isWhitespace(input.charAt(position)))
                break;
            position++;
        }
    }

    protected boolean isEOL(int c) {
        return (c == '\n' || c == '\r');
    }

    // Skip the sequence: <space>*(<comma><space>)?
    // Returns true if we found a comma in there.
    public boolean skipCommaWhitespace() {
        skipWhitespace();
        if (position == inputLength)
            return false;
        if (!(input.charAt(position) == ','))
            return false;
        position++;
        skipWhitespace();
        return true;
    }


    public float nextFloat() {
        float val = numberParser.parseNumber(input, position, inputLength);
        if (!Float.isNaN(val))
            position = numberParser.getEndPos();
        return val;
    }

    /*
     * Scans for a comma-whitespace sequence with a float following it.
     * If found, the float is returned. Otherwise null is returned and
     * the scan position left as it was.
     */
    public float possibleNextFloat() {
        skipCommaWhitespace();
        float val = numberParser.parseNumber(input, position, inputLength);
        if (!Float.isNaN(val))
            position = numberParser.getEndPos();
        return val;
    }

    /*
     * Scans for comma-whitespace sequence with a float following it.
     * But only if the provided 'lastFloat' (representing the last coord
     * scanned was non-null (ie parsed correctly).
     */
    public float checkedNextFloat(float lastRead) {
        if (Float.isNaN(lastRead)) {
            return Float.NaN;
        }
        skipCommaWhitespace();
        return nextFloat();
    }

    public Integer nextInteger() {
        IntegerParser ip = IntegerParser.parseInt(input, position, inputLength);
        if (ip == null)
            return null;
        position = ip.getEndPos();
        return ip.value();
    }

    public Integer nextChar() {
        if (position == inputLength)
            return null;
        return (int) input.charAt(position++);
    }

    public Length nextLength() {
        float scalar = nextFloat();
        if (Float.isNaN(scalar))
            return null;
        Unit unit = nextUnit();
        if (unit == null)
            return new Length(scalar, Unit.px);
        else
            return new Length(scalar, unit);
    }

    /*
     * Scan for a 'flag'. A flag is a '0' or '1' digit character.
     */
    public Boolean nextFlag() {
        if (position == inputLength)
            return null;
        char ch = input.charAt(position);
        if (ch == '0' || ch == '1') {
            position++;
            return Boolean.valueOf(ch == '1');
        }
        return null;
    }

    /*
     * Like checkedNextFloat, but reads a flag (see path definition parser)
     */
    public Boolean checkedNextFlag(Object lastRead) {
        if (lastRead == null) {
            return null;
        }
        skipCommaWhitespace();
        return nextFlag();
    }

    public boolean consume(char ch) {
        boolean found = (position < inputLength && input.charAt(position) == ch);
        if (found)
            position++;
        return found;
    }


    public boolean consume(String str) {
        int len = str.length();
        boolean found = (position <= (inputLength - len) && input.substring(position, position + len).equals(str));
        if (found)
            position += len;
        return found;
    }


    protected int advanceChar() {
        if (position == inputLength)
            return -1;
        position++;
        if (position < inputLength)
            return input.charAt(position);
        else
            return -1;
    }


    /*
     * Scans the input starting immediately at 'position' for the next token.
     * A token is a sequence of characters terminating at a whitespace character.
     * Note that this routine only checks for whitespace characters.  Use nextToken(char)
     * if token might end with another character.
     */
    public String nextToken() {
        return nextToken(' ');
    }

    /*
     * Scans the input starting immediately at 'position' for the next token.
     * A token is a sequence of characters terminating at either a whitespace character
     * or the supplied terminating character.
     */
    public String nextToken(char terminator) {
        if (empty())
            return null;

        int ch = input.charAt(position);
        if (isWhitespace(ch) || ch == terminator)
            return null;

        int start = position;
        ch = advanceChar();
        while (ch != -1 && ch != terminator && !isWhitespace(ch)) {
            ch = advanceChar();
        }
        return input.substring(start, position);
    }

    /*
     * Scans the input starting immediately at 'position' for the a sequence
     * of letter characters terminated by an open bracket.  The function
     * name is returned.
     */
    public String nextFunction() {
        if (empty())
            return null;
        int start = position;

        int ch = input.charAt(position);
        while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
            ch = advanceChar();
        int end = position;
        while (isWhitespace(ch))
            ch = advanceChar();
        if (ch == '(') {
            position++;
            return input.substring(start, end);
        }
        position = start;
        return null;
    }

    /*
     * Get the next few chars. Mainly used for error messages.
     */
    public String ahead() {
        int start = position;
        while (!empty() && !isWhitespace(input.charAt(position)))
            position++;
        String str = input.substring(start, position);
        position = start;
        return str;
    }

    public Unit nextUnit() {
        if (empty())
            return null;
        int ch = input.charAt(position);
        if (ch == '%') {
            position++;
            return Unit.percent;
        }
        if (position > (inputLength - 2))
            return null;
        try {
            Unit result = Unit.valueOf(input.substring(position, position + 2).toLowerCase(Locale.US));
            position += 2;
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /*
     * Check whether the next character is a letter.
     */
    public boolean hasLetter() {
        if (position == inputLength)
            return false;
        char ch = input.charAt(position);
        return ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
    }

    /*
     * Extract a quoted string from the input.
     */
    public String nextQuotedString() {
        if (empty())
            return null;
        int start = position;
        int ch = input.charAt(position);
        int endQuote = ch;
        if (ch != '\'' && ch != '"')
            return null;
        ch = advanceChar();
        while (ch != -1 && ch != endQuote)
            ch = advanceChar();
        if (ch == -1) {
            position = start;
            return null;
        }
        position++;
        return input.substring(start + 1, position - 1);
    }

    /*
     * Return the remaining input as a string.
     */
    public String restOfText() {
        if (empty())
            return null;

        int start = position;
        position = inputLength;
        return input.substring(start);
    }

    public static Path parserPath(String val) {
        TextScanner scan = new TextScanner(val);

        int pathCommand = '?';
        float currentX = 0f, currentY = 0f;    // The last point visited in the subpath
        float lastMoveX = 0f, lastMoveY = 0f;  // The initial point of current subpath
        float lastControlX = 0f, lastControlY = 0f;  // Last control point of the just completed bezier curve.
        float x, y, x1, y1, x2, y2;
        float rx, ry, xAxisRotation;
        Boolean largeArcFlag, sweepFlag;

        Path path = new Path();

        if (scan.empty())
            return path;

        pathCommand = scan.nextChar();

        if (pathCommand != 'M' && pathCommand != 'm')
            return path;  // Invalid path - doesn't start with a move

        while (true) {
            scan.skipWhitespace();

            switch (pathCommand) {
                // Move
                case 'M':
                case 'm':
                    x = scan.nextFloat();
                    y = scan.checkedNextFloat(x);
                    if (Float.isNaN(y)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    // Relative moveto at the start of a path is treated as an absolute moveto.
                    if (pathCommand == 'm' && !path.isEmpty()) {
                        x += currentX;
                        y += currentY;
                    }
                    path.moveTo(x, y);
                    currentX = lastMoveX = lastControlX = x;
                    currentY = lastMoveY = lastControlY = y;
                    // Any subsequent coord pairs should be treated as a lineto.
                    pathCommand = (pathCommand == 'm') ? 'l' : 'L';
                    break;

                // Line
                case 'L':
                case 'l':
                    x = scan.nextFloat();
                    y = scan.checkedNextFloat(x);
                    if (Float.isNaN(y)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand == 'l') {
                        x += currentX;
                        y += currentY;
                    }
                    path.lineTo(x, y);
                    currentX = lastControlX = x;
                    currentY = lastControlY = y;
                    break;

                // Cubic bezier
                case 'C':
                case 'c':
                    x1 = scan.nextFloat();
                    y1 = scan.checkedNextFloat(x1);
                    x2 = scan.checkedNextFloat(y1);
                    y2 = scan.checkedNextFloat(x2);
                    x = scan.checkedNextFloat(y2);
                    y = scan.checkedNextFloat(x);
                    if (Float.isNaN(y)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand == 'c') {
                        x += currentX;
                        y += currentY;
                        x1 += currentX;
                        y1 += currentY;
                        x2 += currentX;
                        y2 += currentY;
                    }
                    path.cubicTo(x1, y1, x2, y2, x, y);
                    lastControlX = x2;
                    lastControlY = y2;
                    currentX = x;
                    currentY = y;
                    break;

                // Smooth curve (first control point calculated)
                case 'S':
                case 's':
                    x1 = 2 * currentX - lastControlX;
                    y1 = 2 * currentY - lastControlY;
                    x2 = scan.nextFloat();
                    y2 = scan.checkedNextFloat(x2);
                    x = scan.checkedNextFloat(y2);
                    y = scan.checkedNextFloat(x);
                    if (Float.isNaN(y)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand == 's') {
                        x += currentX;
                        y += currentY;
                        x2 += currentX;
                        y2 += currentY;
                    }
                    path.cubicTo(x1, y1, x2, y2, x, y);
                    lastControlX = x2;
                    lastControlY = y2;
                    currentX = x;
                    currentY = y;
                    break;

                // Close path
                case 'Z':
                case 'z':
                    path.close();
                    currentX = lastControlX = lastMoveX;
                    currentY = lastControlY = lastMoveY;
                    break;

                // Horizontal line
                case 'H':
                case 'h':
                    x = scan.nextFloat();
                    if (Float.isNaN(x)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand == 'h') {
                        x += currentX;
                    }
                    path.lineTo(x, currentY);
                    currentX = lastControlX = x;
                    break;

                // Vertical line
                case 'V':
                case 'v':
                    y = scan.nextFloat();
                    if (Float.isNaN(y)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand == 'v') {
                        y += currentY;
                    }
                    path.lineTo(currentX, y);
                    currentY = lastControlY = y;
                    break;

                // Quadratic bezier
                case 'Q':
                case 'q':
                    x1 = scan.nextFloat();
                    y1 = scan.checkedNextFloat(x1);
                    x = scan.checkedNextFloat(y1);
                    y = scan.checkedNextFloat(x);
                    if (Float.isNaN(y)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand == 'q') {
                        x += currentX;
                        y += currentY;
                        x1 += currentX;
                        y1 += currentY;
                    }
                    path.quadTo(x1, y1, x, y);
                    lastControlX = x1;
                    lastControlY = y1;
                    currentX = x;
                    currentY = y;
                    break;

                // Smooth quadratic bezier
                case 'T':
                case 't':
                    x1 = 2 * currentX - lastControlX;
                    y1 = 2 * currentY - lastControlY;
                    x = scan.nextFloat();
                    y = scan.checkedNextFloat(x);
                    if (Float.isNaN(y)) {
                        //Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand == 't') {
                        x += currentX;
                        y += currentY;
                    }
                    path.quadTo(x1, y1, x, y);
                    lastControlX = x1;
                    lastControlY = y1;
                    currentX = x;
                    currentY = y;
                    break;

                // Arc
                case 'A':
                case 'a':
                    rx = scan.nextFloat();
                    ry = scan.checkedNextFloat(rx);
                    xAxisRotation = scan.checkedNextFloat(ry);
                    largeArcFlag = scan.checkedNextFlag(xAxisRotation);
                    sweepFlag = scan.checkedNextFlag(largeArcFlag);
                    x = scan.checkedNextFloat(0);
                    y = scan.checkedNextFloat(x);
                    if (Float.isNaN(y) || rx < 0 || ry < 0) {
                        Log.e(TAG, "Bad path coords for "+((char)pathCommand)+" path segment");
                        return path;
                    }
                    if (pathCommand=='a') {
                        x += currentX;
                        y += currentY;
                    }
                    arcTo(currentX, currentY, rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y, path);
                    //path.arcTo(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y);
                    currentX = lastControlX = x;
                    currentY = lastControlY = y;
                    break;

                default:
                    return path;
            }

            scan.skipCommaWhitespace();
            if (scan.empty())
                break;

            // Test to see if there is another set of coords for the current path command
            if (scan.hasLetter()) {
                // Nope, so get the new path command instead
                pathCommand = scan.nextChar();
            }
        }
        return path;
    }

    private static void arcTo(float lastX, float lastY, float rx, float ry, float angle, boolean largeArcFlag, boolean sweepFlag, float x, float y, Path pather)
    {
        if(lastX != x || lastY != y) {
            if(rx != 0.0F && ry != 0.0F) {
                rx = Math.abs(rx);
                ry = Math.abs(ry);
                float angleRad = (float)Math.toRadians((double)angle % 360.0D);
                double cosAngle = Math.cos((double)angleRad);
                double sinAngle = Math.sin((double)angleRad);
                double dx2 = (double)(lastX - x) / 2.0D;
                double dy2 = (double)(lastY - y) / 2.0D;
                double x1 = cosAngle * dx2 + sinAngle * dy2;
                double y1 = -sinAngle * dx2 + cosAngle * dy2;
                double rx_sq = (double)(rx * rx);
                double ry_sq = (double)(ry * ry);
                double x1_sq = x1 * x1;
                double y1_sq = y1 * y1;
                double radiiCheck = x1_sq / rx_sq + y1_sq / ry_sq;
                if(radiiCheck > 1.0D) {
                    rx = (float)Math.sqrt(radiiCheck) * rx;
                    ry = (float)Math.sqrt(radiiCheck) * ry;
                    rx_sq = (double)(rx * rx);
                    ry_sq = (double)(ry * ry);
                }

                double sign = (double)(largeArcFlag == sweepFlag?-1:1);
                double sq = (rx_sq * ry_sq - rx_sq * y1_sq - ry_sq * x1_sq) / (rx_sq * y1_sq + ry_sq * x1_sq);
                sq = sq < 0.0D?0.0D:sq;
                double coef = sign * Math.sqrt(sq);
                double cx1 = coef * ((double)rx * y1 / (double)ry);
                double cy1 = coef * -((double)ry * x1 / (double)rx);
                double sx2 = (double)(lastX + x) / 2.0D;
                double sy2 = (double)(lastY + y) / 2.0D;
                double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
                double cy = sy2 + sinAngle * cx1 + cosAngle * cy1;
                double ux = (x1 - cx1) / (double)rx;
                double uy = (y1 - cy1) / (double)ry;
                double vx = (-x1 - cx1) / (double)rx;
                double vy = (-y1 - cy1) / (double)ry;
                double n = Math.sqrt(ux * ux + uy * uy);
                sign = uy < 0.0D?-1.0D:1.0D;
                double angleStart = Math.toDegrees(sign * Math.acos(ux / n));
                n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
                double p = ux * vx + uy * vy;
                sign = ux * vy - uy * vx < 0.0D?-1.0D:1.0D;
                double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
                if(!sweepFlag && angleExtent > 0.0D) {
                    angleExtent -= 360.0D;
                } else if(sweepFlag && angleExtent < 0.0D) {
                    angleExtent += 360.0D;
                }

                angleExtent %= 360.0D;
                angleStart %= 360.0D;
                float[] bezierPoints = arcToBeziers(angleStart, angleExtent);
                Matrix m = new Matrix();
                m.postScale(rx, ry);
                m.postRotate(angle);
                m.postTranslate((float)cx, (float)cy);
                m.mapPoints(bezierPoints);
                bezierPoints[bezierPoints.length - 2] = x;
                bezierPoints[bezierPoints.length - 1] = y;

                for(int i = 0; i < bezierPoints.length; i += 6) {
                    pather.cubicTo(bezierPoints[i], bezierPoints[i + 1], bezierPoints[i + 2], bezierPoints[i + 3], bezierPoints[i + 4], bezierPoints[i + 5]);
                }

            } else {
                pather.lineTo(x, y);
            }
        }
    }


    /*
     * Generate the control points and endpoints for a set of bezier curves that match
     * a circular arc starting from angle 'angleStart' and sweep the angle 'angleExtent'.
     * The circle the arc follows will be centred on (0,0) and have a radius of 1.0.
     *
     * Each bezier can cover no more than 90 degrees, so the arc will be divided evenly
     * into a maximum of four curves.
     *
     * The resulting control points will later be scaled and rotated to match the final
     * arc required.
     *
     * The returned array has the format [x0,y0, x1,y1,...] and excludes the start point
     * of the arc.
     */
    private static float[]  arcToBeziers(double angleStart, double angleExtent)
    {
//        int numSegments = (int)Math.ceil(Math.abs(angleExtent) / 90.0D);
//        angleStart = Math.toRadians(angleStart);
//        angleExtent = Math.toRadians(angleExtent);
//        float angleIncrement = (float)(angleExtent / (double)numSegments);
//        double controlLength = 1.3333333333333333D * Math.sin((double)angleIncrement / 2.0D) / (1.0D + Math.cos((double)angleIncrement / 2.0D));
//        float[] coords = new float[numSegments * 6];
//        int pos = 0;
//
//        for(int i = 0; i < numSegments; ++i) {
//            double angle = angleStart + (double)((float)i * angleIncrement);
//            double dx = Math.cos(angle);
//            double dy = Math.sin(angle);
//            coords[pos++] = (float)(dx - controlLength * dy);
//            coords[pos++] = (float)(dy + controlLength * dx);
//            angle += (double)angleIncrement;
//            dx = Math.cos(angle);
//            dy = Math.sin(angle);
//            coords[pos++] = (float)(dx + controlLength * dy);
//            coords[pos++] = (float)(dy - controlLength * dx);
//            coords[pos++] = (float)dx;
//            coords[pos++] = (float)dy;
//        }
//
//        return coords;
        int    numSegments = (int) Math.ceil(Math.abs(angleExtent) / 90.0);

        angleStart = Math.toRadians(angleStart);
        angleExtent = Math.toRadians(angleExtent);
        float  angleIncrement = (float) (angleExtent / numSegments);

        // The length of each control point vector is given by the following formula.
        double  controlLength = 4.0 / 3.0 * Math.sin(angleIncrement / 2.0) / (1.0 + Math.cos(angleIncrement / 2.0));

        float[] coords = new float[numSegments * 6];
        int     pos = 0;

        for (int i=0; i<numSegments; i++)
        {
            double  angle = angleStart + i * angleIncrement;
            // Calculate the control vector at this angle
            double  dx = Math.cos(angle);
            double  dy = Math.sin(angle);
            // First control point
            coords[pos++]   = (float) (dx - controlLength * dy);
            coords[pos++] = (float) (dy + controlLength * dx);
            // Second control point
            angle += angleIncrement;
            dx = Math.cos(angle);
            dy = Math.sin(angle);
            coords[pos++] = (float) (dx + controlLength * dy);
            coords[pos++] = (float) (dy - controlLength * dx);
            // Endpoint of bezier
            coords[pos++] = (float) dx;
            coords[pos++] = (float) dy;
        }
        return coords;
    }

    public static CharSequence zoomPath(String val, float ratioWidth, float ratioHeight) {
        TextScanner scan = new TextScanner(val);

        int pathCommand;
        float xAxisRotation;
        Boolean largeArcFlag, sweepFlag;

        StringBuilder path = new StringBuilder();

        if (scan.empty())
            return path;

        pathCommand = scan.nextChar();

        if (pathCommand != 'M' && pathCommand != 'm')
            return path;  // Invalid path - doesn't start with a move

        while (true) {
            scan.skipWhitespace();

            switch (pathCommand) {
                // Move
                case 'M':
                case 'm':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    // Any subsequent coord pairs should be treated as a lineto.
                    pathCommand = (pathCommand == 'm') ? 'l' : 'L';
                    break;
                // Line
                case 'L':
                case 'l':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    break;

                // Cubic bezier
                case 'C':
                case 'c':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    path.append(' ');
                    path.append(scan.checkedNextFloat(0)*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    path.append(' ');
                    path.append(scan.checkedNextFloat(0)*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    break;

                // Smooth curve (first control point calculated)
                case 'S':
                case 's':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    path.append(' ');
                    path.append(scan.checkedNextFloat(0)*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    break;

                // Close path
                case 'Z':
                case 'z':
                    path.append((char) pathCommand);
                    break;

                // Horizontal line
                case 'H':
                case 'h':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioWidth);
                    break;

                // Vertical line
                case 'V':
                case 'v':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioHeight);
                    break;

                // Quadratic bezier
                case 'Q':
                case 'q':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    path.append(' ');
                    path.append(scan.checkedNextFloat(0)*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    break;

                // Smooth quadratic bezier
                case 'T':
                case 't':
                    path.append((char) pathCommand);
                    path.append(scan.nextFloat()*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);
                    break;

                // Arc
                case 'A':
                case 'a':
                    path.append((char) pathCommand);
//                    rx = scan.nextFloat();
//                    ry = scan.checkedNextFloat(rx);
                    path.append(scan.nextFloat()*ratioWidth);
                    path.append(',');
                    path.append(scan.checkedNextFloat(0)*ratioHeight);


                    xAxisRotation = scan.checkedNextFloat(0);
                    largeArcFlag = scan.checkedNextFlag(xAxisRotation);
                    sweepFlag = scan.checkedNextFlag(largeArcFlag);
                    path.append(' ');
                    path.append(xAxisRotation);
                    path.append(' ');
                    path.append(largeArcFlag == null ? 0 : 1);
                    path.append(',');
                    path.append(sweepFlag == null ? 0 : 1);

                    if (sweepFlag != null) {
//                        x = scan.possibleNextFloat();
//                        y = scan.checkedNextFloat(x);
                        path.append(' ');
                        path.append(scan.possibleNextFloat()*ratioWidth);
                        path.append(',');
                        path.append(scan.checkedNextFloat(0)*ratioHeight);
                    }
                    break;

                default:
                    return path;
            }

            scan.skipCommaWhitespace();
            if (scan.empty())
                break;

            // Test to see if there is another set of coords for the current path command
            if (scan.hasLetter()) {
                // Nope, so get the new path command instead
                pathCommand = scan.nextChar();
            }
        }
        return path;
    }
}
