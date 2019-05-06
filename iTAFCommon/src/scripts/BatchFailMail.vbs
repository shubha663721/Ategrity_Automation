strData = WScript.Arguments(0)


arrData = split (strData,"#")


sendToEmail = arrData(0)
strSubject = arrData(1)
Body = arrData(2)



call SendMailOutlook(SendToEmail, strSubject, Body)

Function SendMailOutlook(SendToEmail, Subject, Body)

    Set ol=CreateObject("Outlook.Application")
    Set Mail=ol.CreateItem(0)
    Mail.to=SendToEmail
    Mail.Subject=Subject
    Mail.Body=Body
    Mail.Send
    'ol.Quit
    Set Mail = Nothing
    Set ol = Nothing
End Function


 
