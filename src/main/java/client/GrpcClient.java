package client;

import deepthought.BootRequest;
import deepthought.BootResponse;
import deepthought.ComputeGrpc;
import io.grpc.Context;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GrpcClient {
    public static void main(String[] args) throws InterruptedException {
        boot();
    }

    private static void boot() {
        var channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        var stub = ComputeGrpc.newStub(channel);
        Context.current().withDeadlineAfter(2500, TimeUnit.MILLISECONDS, Executors.newScheduledThreadPool(0)).run(() -> {
            stub.boot(BootRequest.newBuilder().build(), new StreamObserver<>() {
                @Override
                public void onNext(BootResponse value) {
                    System.out.printf("Boot: %s\n", value.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    System.err.print("receiving boot response: " + t.toString() + "\n");
                }

                @Override
                public void onCompleted() {
                }
            });
        });
    }
}
