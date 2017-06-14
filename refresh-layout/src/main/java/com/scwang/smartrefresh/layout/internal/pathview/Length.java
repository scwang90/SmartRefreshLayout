package com.scwang.smartrefresh.layout.internal.pathview;

class Length implements Cloneable
    {
        float  value = 0;
        Unit unit = Unit.px;

        public Length(float value, Unit unit)
        {
            this.value = value;
            this.unit = unit;
        }

        public Length(float value)
        {
            this.value = value;
            this.unit = Unit.px;
        }

        public float floatValue()
        {
            return value;
        }

        // For situations (like calculating the initial viewport) when we can only rely on
        // physical real world units.
        public float floatValue(float dpi)
        {
            switch (unit)
            {
                case px:
                    return value;
                case in:
                    return value * dpi;
                case cm:
                    return value * dpi / 2.54f;
                case mm:
                    return value * dpi / 25.4f;
                case pt: // 1 point = 1/72 in
                    return value * dpi / 72f;
                case pc: // 1 pica = 1/6 in
                    return value * dpi / 6f;
                case em:
                case ex:
                case percent:
                default:
                    return value;
            }
        }

        public boolean isZero()
        {
            return value == 0f;
        }

        public boolean isNegative()
        {
            return value < 0f;
        }

        @Override
        public String toString()
        {
            return String.valueOf(value) + unit;
        }
    }
