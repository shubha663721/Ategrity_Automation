Dim objWMIService, objProcess, colProcess

Set objWMIService = GetObject("winmgmts:" & "{impersonationLevel=impersonate}!\\.\root\cimv2") 
Set colProcess = objWMIService.ExecQuery ("Select * from Win32_Process Where Name = 'EXCEL.EXE'")

For Each objProcess in colProcess
    objProcess.Terminate()
Next

