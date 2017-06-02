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

package com.scwang.smartrefreshlayout.internal.pathview;

/**
 * Parse a SVG/CSS 'integer' or hex number from a String.
 * 
 * We use our own parser to gain a bit of speed.  This routine is
 * around twice as fast as the system one.
 * 
 */

public class IntegerParser
{
   int      pos;
   boolean  isNegative;
   long     value;


   public  IntegerParser(boolean isNegative, long value, int pos)
   {
      this.isNegative = isNegative;
      this.value = value;
      this.pos = pos;
   }


   /*
    * Return the value of pos after the parse.
    */
   public int  getEndPos()
   {
      return this.pos;
   }


   /*
    * Scan the string for an SVG integer.
    */
   public static IntegerParser  parseInt(String str)
   {
      return parseInt(str, 0, str.length());
   }


   /*
    * Scan the string for an SVG integer.
    * Assumes maxPos will not be greater than input.length().
    */
   public static IntegerParser  parseInt(String input, int startpos, int len)
   {
      int      pos = startpos;
      boolean  isNegative = false;
      long     value = 0;
      int      sigStart = 0;


      if (pos >= len)
        return null;  // String is empty - no number found

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
         if (ch >= '0' && ch <= '9')
         {
            if (isNegative) {
               value = value * 10 - ((int)ch - (int)'0');
               if (value < Integer.MIN_VALUE)
                  return null;
            } else {
               value = value * 10 + ((int)ch - (int)'0');
               if (value > Integer.MAX_VALUE)
                  return null;
            }
         }
         else
            break;
         pos++;
      }

      // Have we seen anything number-ish at all so far?
      if (pos == sigStart) {
         return null;
      }

      return new IntegerParser(isNegative, value, pos);
   }


   /*
    * Return the parsed value as an actual float.
    */
   public int  value()
   {
      return (int)value;
   }


   /*
    * Scan the string for an SVG hex integer.
    */
   public static IntegerParser  parseHex(String str)
   {
      return parseHex(str, 0, str.length());
   }


   /*
    * Scan the string for an SVG hex integer.
    * Assumes maxPos will not be greater than input.length().
    */
   public static IntegerParser  parseHex(String input, int startpos, int len)
   {
      int   pos = startpos;
      long  value = 0;
      char  ch;


      if (pos >= len)
        return null;  // String is empty - no number found

      while (pos < len)
      {
         ch = input.charAt(pos);
         if (ch >= '0' && ch <= '9')
         {
            value = value * 16 + ((int)ch - (int)'0');
         }
         else if (ch >= 'A' && ch <= 'F')
         {
            value = value * 16 + ((int)ch - (int)'A') + 10;
         }
         else if (ch >= 'a' && ch <= 'f')
         {
            value = value * 16 + ((int)ch - (int)'a') + 10;
         }
         else
            break;

         if (value > 0xffffffffL)
            return null;

         pos++;
      }

      // Have we seen anything number-ish at all so far?
      if (pos == startpos) {
         return null;
      }

      return new IntegerParser(false, value, pos);
   }

}
