/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_3
   Params: cStr
   Return: Variable
  Example: EXT_3(cStr)
  ..........................................................................
*/

function EXT_3(cStr)

   local nNum, nNum1, nNum2, cStr1

   cStr := if(VALTYPE(cStr) = "N", STR(cStr, 3), cStr)
   cStr1 := ""

   if VAL(cStr) <> 0

      nNum := VAL(SUBSTR(cStr, 1, 1))
      nNum1 := VAL(SUBSTR(cStr, 2, 1))
      nNum2 := VAL(SUBSTR(cStr, 3, 1))

      if nNum <> 0

         if nNum1 + nNum2 = 0 .and. nNum = 1

            cStr1 := cStr1 + "cem "

         else

            cStr1 := cStr1 + ({"cento", "duzentos", "trezentos", ;
                  "quatrocentos", "quinhentos", "seicentos", "setecentos", ;
                  "oitocentos", "novecentos"})[nNum] + " "

         endif

         if nNum1 + nNum2 <> 0

            cStr1 := cStr1 + "e "

         endif

      endif

      if nNum1 = 1 .and. nNum2 <> 0

         cStr1 := cStr1 + ({"onze", "doze", "treze", "quatroze", "quinze", ;
               "dezesseis", "dezessete", "dezoito", "dezenove"})[nNum2] + ;
               " "

      else

         if nNum1 <> 0

            cStr1 := cStr1 + ({"dez", "vinte", "trinta", "quarenta", ;
                  "cincoenta", "sessenta", "setenta", "oitenta", "noventa"};
                  )[nNum1] + " "

            if nNum2 <> 0

               cStr1 := cStr1 + "e "

            endif

         endif

         if nNum2 <> 0

            cStr1 := cStr1 + ({"um", "dois", "tres", "quatro", "cinco", ;
                  "seis", "sete", "oito", "nove"})[nNum2] + " "

         endif

      endif

   endif


return cStr1

