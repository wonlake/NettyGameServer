using SuperSocket.ClientEngine;
using SuperSocket.ProtoBase;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using System;
using System.Net;
using Google.Protobuf;
using System.IO;

public class ProtoBufPackageInfo : IPackageInfo
{
    public ProtoBufPackageInfo(MessageWrapper msg)
    {
        Msg = msg;
    }

    public MessageWrapper Msg { get; private set; }
}

class ProtoBufReceiveFilter: IReceiveFilter<ProtoBufPackageInfo>
{
    public ProtoBufPackageInfo Filter(BufferList data, out int rest)
    {
        rest = 0;
        var buffStream = new BufferStream();
        buffStream.Initialize(data);

        var stream = new CodedInputStream(buffStream);
        var varint32 = (int)stream.ReadLength();
        if (varint32 <= 0)
            return default(ProtoBufPackageInfo);
        
        var total = data.Total;
        var packageLen = varint32 + (int)stream.Position;

        if (total >= packageLen)
        {
            rest = total - packageLen;
            stream = new CodedInputStream(buffStream);
            var body = stream.ReadBytes();
            var message = MessageWrapper.Parser.ParseFrom(body);
            var requestInfo = new ProtoBufPackageInfo(message);
            return requestInfo;
        }
        return default(ProtoBufPackageInfo);
    }

    public IReceiveFilter<ProtoBufPackageInfo> NextReceiveFilter { get; protected set; }
    public FilterState State { get; protected set; }

    public void Reset()
    {
        NextReceiveFilter = null;
        State = FilterState.Normal;
    }
}

public class Test : MonoBehaviour {

    private MessageWrapper m_MsgWrapper = new MessageWrapper();
    EasyClient client;
    // Use this for initialization
    public string m_PlayerName = "战神";
    public int m_Age = 100;
    public string m_ServerIP = "172.17.70.161";
    public short m_ServerPort = 9090;

	void Start () {
        
    }
	
	// Update is called once per frame
	void Update () {
		
	}

    void OnApplicationQuit()
    {
        if (client.IsConnected)
            client.Close();
    }
    public void SendText()
    {
        if (client == null)
        {
            client = new EasyClient();
            client.Initialize(new ProtoBufReceiveFilter(),
                (msgWrapper) =>
                {
                    var person = Person.Parser.ParseFrom(msgWrapper.Msg.Message);
                    Debug.Log(person.Name);
                    Debug.Log(person.Age);
                }); 
        }

        if(!client.IsConnected)
        { 
            client.BeginConnect(new IPEndPoint(IPAddress.Parse(m_ServerIP), m_ServerPort));
            return;
        }

        Person newPerson = new Person { Name = m_PlayerName, Age = m_Age};
        m_MsgWrapper.Id = 200;
        m_MsgWrapper.Message = newPerson.ToByteString();
        using (MemoryStream stream = new MemoryStream())
        {
            var os = new CodedOutputStream(stream);
            os.WriteBytes(m_MsgWrapper.ToByteString());
            os.Flush();
            var data = stream.ToArray();
            client.Send(data); 
        }
    }
}
