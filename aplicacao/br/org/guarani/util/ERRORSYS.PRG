/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: ERRORSYS
   Params: None.
   Return: Undefined
  Example: ERRORSYS()
  ..........................................................................
*/

procedure ERRORSYS()

   ERRORBLOCK({|_bDef|DEFERROR(_bDef)})

return 

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: DEFERROR
   Params: oObj
   Return: Variable
  Example: DEFERROR(oObj)
  ..........................................................................
*/

static function DEFERROR(oObj)

   local nNum, cStr, aArr, nNum1

   if oObj:GENCODE() == 5

      return 0

   endif

   if oObj:GENCODE() == 21 .and. oObj:OSCODE() == 32 .and. oObj:CANDEFAULT(;
         )

      NETERR(.t.)
      return .f.

   endif

   if oObj:GENCODE() == 40 .and. oObj:CANDEFAULT()

      NETERR(.t.)
      return .f.

   endif

   cStr := ERRORMESSA(oObj)
   aArr := {"Quit"}

   if oObj:CANRETRY()

      AADD(aArr, "Retry")

   endif

   if oObj:CANDEFAULT()

      AADD(aArr, "Default")

   endif

   nNum1 := 0

   do while nNum1 == 0

      if EMPTY(oObj:OSCODE())

         nNum1 := ALERT(cStr, aArr)

      else

         nNum1 := ALERT(cStr + ";(DOS Error " + LTRIM(STR(oObj:OSCODE())) +;
                ")", aArr)

      endif

      if nNum1 == nil

         exit

      endif

   enddo

   if  .not. EMPTY(nNum1)

      if aArr[nNum1] == "Break"

         break( oObj )

      elseif aArr[nNum1] == "Retry"

         return .t.

      elseif aArr[nNum1] == "Default"

         return .f.

      endif

   endif

   if  .not. EMPTY(oObj:OSCODE())

      cStr := cStr + (" (DOS Error " + LTRIM(STR(oObj:OSCODE())) + ") ")

   endif

   OUTERR(CHR(13)+CHR(10))
   OUTERR(cStr)
   nNum := 2

   do while  .not. EMPTY(PROCNAME(nNum))

      OUTERR(CHR(13)+CHR(10))
      OUTERR("Called from", TRIM(PROCNAME(nNum)) + "(" + LTRIM(STR(;
            PROCLINE(nNum))) + ")  ")
      ++nNum

   enddo

   ERRORLEVEL(1)
   __QUIT()

return .f.

/* ------ Rescue5 1.00 (c) APTware 1993,94 ------
     Name: ERRORMESSA
   Params: oObj
   Return: Variable
  Example: ERRORMESSA(oObj)
  ..........................................................................
*/

static function ERRORMESSA(oObj)

   local vVar := if(oObj:SEVERITY() > 1, "Error ", "Warning ")

   if VALTYPE(oObj:SUBSYSTEM()) == "C"

      vVar := vVar + oObj:SUBSYSTEM()

   else

      vVar := vVar + "???"

   endif

   if VALTYPE(oObj:SUBCODE()) == "N"

      vVar := vVar + ("/" + LTRIM(STR(oObj:SUBCODE())))

   else

      vVar := vVar + "/???"

   endif

   if VALTYPE(oObj:DESCRIPTIO()) == "C"

      vVar := vVar + ("  " + oObj:DESCRIPTIO())

   endif

   if  .not. EMPTY(oObj:FILENAME())

      vVar := vVar + (": " + oObj:FILENAME())

   elseif  .not. EMPTY(oObj:OPERATION())

      vVar := vVar + (": " + oObj:OPERATION())

   endif


return vVar

