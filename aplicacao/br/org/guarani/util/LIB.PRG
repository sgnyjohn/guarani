/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_INT
   Params: _Def
   Return: Variable
  Example: EXT_INT(_Def)
  ..........................................................................
*/

function EXT_INT(_Def)

   local _Def1, nNum := 1
   local vVar := ""
   local cStr

   do while nNum < 6

      cStr := SUBSTR(_Def, nNum * 3 - 2, 3)
      vVar := vVar + EXT_3(cStr)

      if VAL(cStr) <> 0 .and. nNum <> 5

         if VAL(cStr) > 1

            vVar := vVar + ({"mil", "milhoes", "bilhoes", "trilhoes"})[5 - ;
                  nNum]

         else

            vVar := vVar + ({"mil", "milhao", "bilhao", "trilhao"})[5 - ;
                  nNum]

         endif

         if VAL(SUBSTR(_Def + "    ", nNum * 3 + 1)) <> 0

            if VAL(SUBSTR(LEFT(_Def, 12) + SPACE(7), nNum * 3 + 1)) <> 0

               vVar := vVar + ", "

            else

               vVar := vVar + " e "

            endif

         else

            vVar := vVar + " "

         endif

      endif

      nNum := nNum + 1

   enddo


return vVar

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_MD
   Params: _Def
   Return: String
  Example: EXT_MD(_Def)
  ..........................................................................
*/

function EXT_MD(_Def)

   local vVar := STR(INT(_Def), 15)
   local vVar1 := EXT_INT(vVar)

   if  .not. EMPTY(vVar1)

      vVar1 := vVar1 + if(SUBSTR(vVar, 10) = "000000", "de ", "") + if(INT(;
            _Def) > 1, "cruzeiros reais ", "cruzeiro real ")

   endif

   vVar := INT((_Def - INT(_Def) + 0.001) * 100)

   if vVar <> 0

      vVar1 := vVar1 + if(INT(_Def) <> 0, "e ", "") + EXT_3(STR(vVar, 3)) +;
             if(vVar > 1, "centavos", "centavo")

   endif


return TRIM(vVar1)

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_VR
   Params: _Def
   Return: String
  Example: EXT_VR(_Def)
  ..........................................................................
*/

function EXT_VR(_Def)

   local cStr := STR(_Def)
   local vVar := EXT_INT(RIGHT(SPACE(15) + LEFT(cStr, AT(".", cStr + ;
         ".") - 1), 15))

   if "." $ cStr

      vVar := vVar + if(EMPTY(vVar), "zero ", "") + "virgula "
      cStr := SUBSTR(cStr, AT(".", cStr) + 1)

      do while cStr = "0"

         vVar := vVar + "zero "
         cStr := SUBSTR(cStr, 2)

      enddo

      vVar := vVar + EXT_INT(RIGHT(SPACE(15) + cStr, 15))

   endif


return TRIM(vVar)

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_MS
   Params: nNum
   Return: Undefined
  Example: EXT_MS(nNum)
  ..........................................................................
*/

function EXT_MS(nNum)

   nNum := if(VALTYPE(nNum) = "D", MONTH(nNum), nNum)
   nNum := if(nNum < 1 .or. nNum > 12, 0, nNum)

return ({"", "janeiro", "fevereiro", "marco", "abril", "maio", "junho", ;
      "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"})[;
      nNum + 1]

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_DT
   Params: _Def
   Return: Variable
  Example: EXT_DT(_Def)
  ..........................................................................
*/

function EXT_DT(_Def)


return EXT_3(STR(DAY(_Def), 3)) + "de " + EXT_MS(MONTH(_Def)) + " de " + ;
      EXT_INT(STR(YEAR(_Def), 15))

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_DATA
   Params: _Def
   Return: Variable
  Example: EXT_DATA(_Def)
  ..........................................................................
*/

function EXT_DATA(_Def)


return LTRIM(STR(DAY(_Def))) + " de " + EXT_MS(_Def) + " de " + LTRIM(STR(;
      YEAR(_Def)))

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_DSEM
   Params: nNum
   Return: String
  Example: EXT_DSEM(nNum)
  ..........................................................................
*/

function EXT_DSEM(nNum)

   if VALTYPE(nNum) = "D"

      nNum := DOW(nNum)

   endif

   if nNum > 0 .and. nNum < 8

      return ({"domingo", "segunda-feira", "terca-feira", "quarta-feira", ;
            "quinta-feira", "sexta-feira", "sabado"})[nNum]
      return 

   endif


return "????????"

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_DTS
   Params: _Def
   Return: Variable
  Example: EXT_DTS(_Def)
  ..........................................................................
*/

function EXT_DTS(_Def)


return EXT_DSEM(DOW(_Def)) + ", " + EXT_DT(_Def)

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_HR
   Params: _Def
   Return: Variable
  Example: EXT_HR(_Def)
  ..........................................................................
*/

function EXT_HR(_Def)

   local nNum := VAL(SUBSTR(_Def, 1, 2))
   local nNum1 := VAL(SUBSTR(_Def, 4, 2))
   local nNum2 := VAL(SUBSTR(_Def, 7, 2))


return if(nNum <> 0, EXT_3(" " + SUBSTR(_Def, 1, 2)), "zero ") + "hora" + ;
      if(nNum > 1, "s", "") + ", " + if(nNum1 <> 0, EXT_3(" " + SUBSTR(;
      _Def, 4, 2)), "zero ") + "minuto" + if(nNum1 > 1, "s", "") + " e " + ;
      if(nNum2 <> 0, EXT_3(" " + SUBSTR(_Def, 7, 2)), "zero ") + "segundo" ;
      + if(nNum2 > 1, "s", "")

