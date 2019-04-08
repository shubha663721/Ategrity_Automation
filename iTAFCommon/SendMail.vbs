strData = WScript.Arguments(0)
'msgbox strData
'msgbox "strData = " & strData
arrData = split (strData,"#")
'msgbox arrData
'msgbox "arrData() = " & ubound(arrData)
sendToEmail = arrData(0)
Body = arrData(2)
Body1 = split(Body,"$")
Body=Body1(0) & vbLf & Body1(1) &  vbLf & Body1(2) & vbLF & vbLF & Body1(3)
Attachment1 = arrData(3)
Attachment2 = arrData(4)
Attachment3 = arrData(5)
strSubject = arrData(1)
'msgbox "sendToEmail = " & sendToEmail
'msgbox "strSubject = " & strSubject
'msgbox "Body = " & Body
'msgbox "Attachment1 = " & Attachment1
'msgbox "Attachment2 = " & Attachment2
'msgbox "Attachment3 = " & Attachment3
call SendMailOutlook(SendToEmail, strSubject, Body, Attachment1, Attachment2, Attachment3)
Function SendMailOutlook(SendToEmail, Subject, Body, Attachment1, Attachment2, Attachment3)
    'strMailto, Subject, Message, strMailfrom,strAttach
    Set ol=CreateObject("Outlook.Application")
    Set Mail=ol.CreateItem(0)
    Mail.to=SendToEmail
    Mail.Subject=Subject
    Mail.Body=Body
    If trim(Attachment1) <> "" Then
        Mail.Attachments.Add(Attachment1)
    End If
    If trim(Attachment2) <> "" Then
        Mail.Attachments.Add(Attachment2)
    End If
    If trim(Attachment3) <> "" Then
        Mail.Attachments.Add(Attachment3)
    End If
    Mail.Send
    'ol.Quit
    Set Mail = Nothing
    Set ol = Nothing
End Function


 
