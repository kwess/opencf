use strict;
use warnings;

use JSON;
use IO::Socket;

my $socket = IO::Socket::INET->new(PeerAddr => '127.0.0.1', PeerPort => 5678, Proto => 'tcp') or die "Couldn't connect to Server\n";

sendPacket($socket, {
	type => 1,
	agent_encoding => "json",
	agent_id => "perl_agent",
	agent_version => "v_pl.0.1",
	agent_plattform => "windows"
});

while (1) {
	readPacket($socket);
}

sub readPacket {
	my ($sock) = @_;
	my $recv_data;
	my $send_data;
	my $size = 4;
	
    $socket->read($recv_data, $size);
    $size = unpack ('N', $recv_data);
    print "read size: ".$size."\n";
    $socket->read($recv_data, $size);
    print "read data: ".$recv_data."\n";
}

sub sendPacket {
	my ($sock, $scalar) = @_;
	my $json = JSON->new->allow_nonref;
 
	my $pretty_printed = $json->pretty->encode($scalar);
	
	my $size = length($pretty_printed);

	print "send size: ".$size."\n";
	print "send data: ".$pretty_printed."\n";
	
	$sock->send( pack 'N', $size);
	$sock->send($pretty_printed);
}  
    
