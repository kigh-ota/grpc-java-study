package server;

import deepthought.BootResponse;
import deepthought.ComputeGrpc;
import deepthought.InferResponse;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class ComputeImpl
        extends ComputeGrpc.ComputeImplBase {
    public void boot(
            deepthought.BootRequest request,
            io.grpc.stub.StreamObserver<deepthought.BootResponse> responseObserver) {
        var context = Context.current();
        System.err.printf("Deadline=%s\n", context.getDeadline());
        while (true) {
            if (context.isCancelled()) {
                return;
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            var response = BootResponse.newBuilder().setMessage("I THINK THEREFORE I AM.").build();
            responseObserver.onNext(response);
        }
    }

    public void infer(
            deepthought.InferRequest request,
            io.grpc.stub.StreamObserver<deepthought.InferResponse> responseObserver) {
        switch (request.getQuery()) {
            case "Life":
            case "Universe":
            case "Everything":
                break;
            default:
                responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT));
                return;
        }

        var deadline = Context.current().getDeadline();
        System.err.printf("Deadline=%s\n", deadline);
        if (deadline == null || deadline.timeRemaining(TimeUnit.MILLISECONDS) > 750) {
            var response = InferResponse.newBuilder().setAnswer(42).setDescription(0, "I checked it").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        // https://grpc.github.io/grpc/core/md_doc_statuscodes.html
        responseObserver.onError(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));
    }
}
