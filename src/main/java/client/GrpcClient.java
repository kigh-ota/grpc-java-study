package client;

import deepthought.*;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GrpcClient {
    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
//        boot(channel);
        infer(channel, "Hoge"); // "Hoge"
    }

    private static void boot(@NotNull ManagedChannel channel) {
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

    private static void infer(@NotNull ManagedChannel channel, @NotNull String query) {
        var stub = ComputeGrpc.newBlockingStub(channel);
        Context.current().withDeadlineAfter(1500, TimeUnit.MILLISECONDS, Executors.newScheduledThreadPool(0)).run(() -> {
            try {
                var response = stub.infer(InferRequest.newBuilder().setQuery(query).build());
                System.out.printf("Infer: %s\n", response.getAnswer());
                response.getDescriptionList().forEach(desc -> {
                    System.out.printf("Infer: %s\n", desc);
                });
            } catch (StatusRuntimeException e) {
                System.err.printf("Infer Error: %s\n", e);
            }

        });
    }
}
