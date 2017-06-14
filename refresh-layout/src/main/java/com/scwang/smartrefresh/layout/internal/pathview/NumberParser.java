/*
   Copyright 2014 Paul LeBeau, Cave Rock Software Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.scwang.smartrefresh.layout.internal.pathview;

/**
 * Parse a SVG 'number' or a CSS 'number' from a String.
 * 
 * We use our own parser because the one in Android (from Harmony I think) is slow.
 * 
 * An SVG 'number' is defined as
 *   integer ([Ee] integer)?
 *   | [+-]? [0-9]* "." [0-9]+ ([Ee] integer)?
 * Where 'integer' is
 *   [+-]? [0-9]+
 * CSS numbers were different, but have now been updated to a compatible definition (see 2.1 Errata)
 *   [+-]?([0-9]+|[0-9]*\.[0-9]+)(e[+-]?[0-9]+)?
 * 
 */

public class NumberParser
{
   int      pos;

   static long  TOO_BIG = Long.MAX_VALUE / 10;


   /*
    * Return the value of pos after the parse.
    */
   public int  getEndPos()
   {
      return this.pos;
   }


   /*
    * Scan the string for an SVG number.
    */
   public float  parseNumber(String str)
   {
      return parseNumber(str, 0, str.length());
   }


   /*
    * Scan the string for an SVG number.
    * Assumes maxPos will not be greater than str.length().
    */
   public float  parseNumber(String input, int startpos, int len)
   {
      boolean  isNegative = false;
      long     significand = 0;
      int      numDigits = 0;
      int      numLeadingZeroes = 0;
      int      numTrailingZeroes = 0;
      boolean  decimalSeen = false;
      int      sigStart = 0;
      int      decimalPos = 0;
      int      exponent = 0;

      pos = startpos;

      if (pos >= len)
        return Float.NaN;  // String is empty - no number found

      char  ch = input.charAt(pos);
      switch (ch) {
         case '-': isNegative = true;
                   // fall through
         case '+': pos++;
      }

      sigStart = pos;

      while (pos < len)
      {
         ch = input.charAt(pos);
         if (ch == '0')
         {
            if (numDigits == 0) {
               numLeadingZeroes++;
            } else {
               // We potentially skip trailing zeroes. Keep count for now.
               numTrailingZeroes++;
            }
         }
         else if (ch >= '1' && ch <= '9')
         {
            // Multiply any skipped zeroes into buffer
            numDigits += numTrailingZeroes;
            while (numTrailingZeroes > 0) {
               if (significand > TOO_BIG) {
                  //Log.e("Number is too large");
                  return Float.NaN;
               }
               significand *= 10;
               numTrailingZeroes--;
            }

            if (significand > TOO_BIG) {
               // We will overflow if we continue...
               //Log.e("Number is too large");
               return Float.NaN;
            }
            significand = significand * 10 + ((int)ch - (int)'0');
            numDigits++;
            
            if (significand < 0)
               return Float.NaN;  // overflowed from +ve to -ve
         }
         else if (ch == '.')
         {
            if (decimalSeen) {
               // Stop parsing here.  We may be looking at a new number.
               break;
            }
            decimalPos = pos - sigStart;
            decimalSeen = true;
         }
         else
            break;
         pos++;
      }

      if (decimalSeen && pos == (decimalPos + 1)) {
         // No digits following decimal point (eg. "1.")
         //Log.e("Missing fraction part of number");
         return Float.NaN;
      }

      // Have we seen anything number-ish at all so far?
      if (numDigits == 0) {
         if (numLeadingZeroes == 0) {
            //Log.e("Number not found");
            return Float.NaN;
         }
         // Leading zeroes have been seen though, so we
         // treat that as a '0'.
         numDigits = 1;
      }

      if (decimalSeen) {
         exponent = decimalPos - numLeadingZeroes - numDigits;
      } else {
         exponent = numTrailingZeroes;
      }

      // Now look for exponent
      if (pos < len)
      {
         ch = input.charAt(pos);
         if (ch == 'E' || ch == 'e')
         {
            boolean  expIsNegative = false;
            int      expVal = 0;
            boolean  abortExponent = false;

            pos++;
            if (pos == len) {
               // Incomplete exponent.
               //Log.e("Incomplete exponent of number");
               return Float.NaN;
            }

            switch (input.charAt(pos)) {
               case '-': expIsNegative = true;
                  // fall through
               case '+': pos++;
                  break;
               case '0': case '1': case '2': case '3': case '4':
               case '5': case '6': case '7': case '8': case '9':
                   break; // acceptable next char
               default:
                  // any other character is a failure, ie no exponent.
                  // Could be something legal like "em" though.
                  abortExponent = true;
                  pos--;  // reset pos to position of 'E'/'e'
            }

            if (!abortExponent)
            {
               int  expStart = pos;

               while (pos < len)
               {
                  ch = input.charAt(pos);
                  if (ch >= '0' && ch <= '9')
                  {
                     if (expVal > TOO_BIG) {
                        // We will overflow if we continue...
                        //Log.e("Exponent of number is too large");
                        return Float.NaN;
                     }
                     expVal = expVal * 10 + ((int)ch - (int)'0');
                     pos++;
                  }
                  else
                     break;
               }

               // Check that at least some exponent digits were read
               if (pos == expStart) {
                  //Log.e(""Incomplete exponent of number"");
                  return Float.NaN;
               }

               if (expIsNegative)
                  exponent -= expVal;
               else
                  exponent += expVal;
            }
         }
      }

      // Quick check to eliminate huge exponents.
      // Biggest float is (2 - 2^23) . 2^127 ~== 3.4e38
      // Biggest negative float is 2^-149 ~== 1.4e-45
      // Some numbers that will overflow will get through the scan
      // and be returned as 'valid', yet fail when value() is called.
      // However they will be very rare and not worth slowing down
      // the parse for.
      if ((exponent + numDigits) > 39 || (exponent + numDigits) < -44)
         return Float.NaN;

      float  f = (float) significand;

      if (significand != 0)
      {
         // Do exponents > 0
         if (exponent > 0)
         {
            f *= positivePowersOf10[exponent];
         }
         else if (exponent < 0)
         {
            // Some valid numbers can have an exponent greater than the max (ie. < -38)
            // for a float.  For example, significand=123, exponent=-40
            // If that's the case, we need to apply the exponent in two steps.
            if (exponent < -38) {
               // Long.MAX_VALUE is 19 digits, so taking 20 off the exponent should be enough. 
               f *= 1e-20;
               exponent += 20;
            }
            // Do exponents < 0
            f *= negativePowersOf10[-exponent];
         }
      }

      return (isNegative) ? -f : f;
   }


   private static final float  positivePowersOf10[] = {
      1e0f,  1e1f,  1e2f,  1e3f,  1e4f,  1e5f,  1e6f,  1e7f,  1e8f,  1e9f,
      1e10f, 1e11f, 1e12f, 1e13f, 1e14f, 1e15f, 1e16f, 1e17f, 1e18f, 1e19f,
      1e20f, 1e21f, 1e22f, 1e23f, 1e24f, 1e25f, 1e26f, 1e27f, 1e28f, 1e29f,
      1e30f, 1e31f, 1e32f, 1e33f, 1e34f, 1e35f, 1e36f, 1e37f, 1e38f
   };
   private static final float  negativePowersOf10[] = {
      1e0f,   1e-1f,  1e-2f,  1e-3f,  1e-4f,  1e-5f,  1e-6f,  1e-7f,  1e-8f,  1e-9f,
      1e-10f, 1e-11f, 1e-12f, 1e-13f, 1e-14f, 1e-15f, 1e-16f, 1e-17f, 1e-18f, 1e-19f,
      1e-20f, 1e-21f, 1e-22f, 1e-23f, 1e-24f, 1e-25f, 1e-26f, 1e-27f, 1e-28f, 1e-29f,
      1e-30f, 1e-31f, 1e-32f, 1e-33f, 1e-34f, 1e-35f, 1e-36f, 1e-37f, 1e-38f
   };

}
