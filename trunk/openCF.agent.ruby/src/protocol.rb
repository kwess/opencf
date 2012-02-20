module Protocol
  
  module Type
    AGENT_HELLO = 1
    AGENT_HELLO_RESPONSE = 2
  end
  
  module Keys
    TYPE = "type"
    SUCCESSFULL = "successfull"
    RETURN_CODE = "return_code"
    MESSAGE = "message"
    AGENT_ID = "agent_id"
    AGENT_VERSION = "agent_version"
    AGENT_PLATTFORM = "agent_plattform"
  end
  
  def Protocol.agent_hello(id, version, plattform)
    agent_hello = Hash.new
    agent_hello[Protocol::Keys::TYPE] = Protocol::Type::AGENT_HELLO
    agent_hello[Protocol::Keys::AGENT_ID] = id
    agent_hello[Protocol::Keys::AGENT_VERSION] = version
    agent_hello[Protocol::Keys::AGENT_PLATTFORM] = plattform
    return agent_hello
  end
  
end