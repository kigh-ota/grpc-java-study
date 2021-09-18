package server;

import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(8080).addService(new ComputeImpl()).build();
        server.start();
        server.awaitTermination();
    }
}
