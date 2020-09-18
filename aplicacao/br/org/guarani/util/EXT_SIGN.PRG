
/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: EXT_SIGNO
   Params: nNum, nNum1
   Return: String
  Example: EXT_SIGNO(nNum, nNum1)
  ..........................................................................
*/

function EXT_SIGNO(nNum, nNum1)

   static asArr := {"0120CAPRICORNIO", "0219AQUARIO", "0320PEIXES", ;
      "0420ARIES", "0520TOURO", "0620GEMEOS", "0721CANCER", "0822LEAO", ;
      "0922VIRGEM", "1022LIBRA", "1121ESCORPIAO", "1221SAGITARIO", ;
      "1231CAPRICORNIO"}

   if VALTYPE(nNum) = "C"

      nNum := DT_D(nNum)

   endif

   if VALTYPE(nNum) = "D"

      nNum1 := MONTH(nNum)
      nNum := DAY(nNum)

   endif

   nNum := STRZERO(nNum1, 2) + STRZERO(nNum, 2)
   nNum1 := ASCAN(asArr, {|_bDef|nNum <= LEFT(_bDef, 4)})

   if nNum1 = 0

      return ""

   endif


return SUBSTR(asArr[nNum1], 5)

