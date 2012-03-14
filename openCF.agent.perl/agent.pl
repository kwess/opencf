use strict;
use warnings;

use JSON;
use IO::Socket;
use Config::Properties;
use Log::Log4perl qw(get_logger :levels);


Log::Log4perl->init("log4perl.properties");

my $logger = Log::Log4perl::get_logger();

package Protocol::Types;
	sub INVALID					{return -1;}
	sub AGENT_HELLO 			{return  1;}
	sub AGENT_HELLO_RESPONSE  	{return  2;}
	sub AGENT_HEARTBEAT       	{return  0;}
	sub AUTOMATION_CONTROL    	{return 13;}
	sub AUTOMATION_STATUS     	{return 20;}

package Protocol::Keys;
	sub TYPE 				  {return "type";}
	sub SUCCESSFULL           {return "successfull";}
	sub RETURN_CODE           {return "return_code";}
	sub MESSAGE               {return "message";}
	sub AGENT_ID              {return "agent_id";}
	sub AGENT_VERSION         {return "agent_version";}
	sub AGENT_PLATTFORM       {return "agent_plattform";}
	sub AGENT_ENCODING        {return "agent_encoding";}
	sub AUTOMATION_STATUS     {return "automation_status";}
	sub AUTOMATION_ACTION     {return "automation_action";}
	sub AUTOMATION_ID         {return "automation_id";}
	sub AUTOMATION_MESSAGE    {return "automation_message";}
	sub AUTOMATION_DESCRIPTOR {return "automation_descriptor";}
	sub AUTOMATION_PARAMETER  {return "automation_parameter";}
	sub REPOSTITORY_URL       {return "repository_url";}
	sub LOCAL_TIME            {return "local_time";}



package Connection;

sub new {
	my ($type) = $_[0];
	my ($this) = {host => $_[1], port => $_[2]};

	$logger->debug("host: $_[1]");
	$logger->debug("port: $_[2]");

	my $socket;
	
	bless($this, $type);
	return($this)
}

sub connect {
	my ($this) = $_[0];
	
	$logger->info("connect");
	
	my $socket = IO::Socket::INET->new(
		PeerAddr => $this->{host},
		PeerPort => $this->{port},
		Proto => 'tcp') or die "Couldn't connect to Server\n";
	
	$this->{socket} = $socket;
}

sub readPacket {
	my ($this) = $_[0];
	my $recv_data;
	my $send_data;
	my $size = 4;
	
    $this->{socket}->read($recv_data, $size) or die "cant read size";
    $size = unpack ('N', $recv_data);
    $logger->debug("read size: ".$size."\n");
    $this->{socket}->read($recv_data, $size) or die "cant read data";
    $logger->debug("read data: ".$recv_data."\n");
    
    my $json = JSON->new->allow_nonref;
    
  	$this->handlePacket($json->decode($recv_data));
}

sub handlePacket {
	my ($this) = $_[0];
	my ($packet) = $_[1];
	
	my $type = $packet->{type};
	
	$logger->debug("type: $type");
	
	if ($type eq Protocol::Types::AGENT_HELLO_RESPONSE) {
		$logger->info("agent hello response");
		handleAgentHelloResponse($packet);
	}
}

sub handleAgentHelloResponse {
	my $packet = @_;
	
}

sub sendPacket {
	my ($this) = $_[0];
	my ($scalar) = $_[1];
	my $json = JSON->new->allow_nonref;
 
	my $pretty_printed = $json->encode($scalar);
	
	my $size = length($pretty_printed);

	$logger->debug("send size: ".$size."\n");
	$logger->debug("send data: ".$pretty_printed."\n");
	
	$this->{socket}->send(pack 'N', $size) or die "cant send size";
	$this->{socket}->send($pretty_printed) or die "cant send data";
}

  

package main;

open (my $fh, '<', 'agent.properties') or die ("unable to open configuration file");
my $properties = Config::Properties->new();
$properties->load($fh);
my $version = $properties->getProperty("version");
my $server = $properties->getProperty("server");
my $os = $properties->getProperty("os");
my $port = $properties->getProperty("port");
my $hostname = $properties->getProperty("hostname");


$logger->debug("server: $server");
$logger->debug("port: $port");


my $connection = Connection->new($server, $port);
$connection->connect();
$connection->sendPacket({
	Protocol::Keys::TYPE 			=> 1,
	Protocol::Keys::AGENT_ENCODING 	=> "json",
	Protocol::Keys::AGENT_ID 		=> $hostname,
	Protocol::Keys::AGENT_VERSION 	=> $version,
	Protocol::Keys::AGENT_PLATTFORM	=> $os
});

while (1) {
	$connection->readPacket();
}
    
