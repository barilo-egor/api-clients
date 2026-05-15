package tgb.cryptoexchange.apiclients.controller.handler;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Validator;
import build.buf.protovalidate.ValidatorFactory;
import build.buf.validate.Violation;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import io.grpc.*;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GlobalServerInterceptor
public class GrpcValidationInterceptor implements ServerInterceptor {

    Validator validator = ValidatorFactory.newBuilder().build();

    @Override
    public <R, T> ServerCall.Listener<R> interceptCall(
            ServerCall<R, T> call, Metadata headers, ServerCallHandler<R, T> next) {
        return new ServerCall.Listener<R>() {
            private ServerCall.Listener<R> delegate = null;

            private boolean closed = false;

            @Override
            public void onMessage(R message) {
                if (closed)
                    return;

                if (message instanceof Message protobufMessage) {
                    try {
                        ValidationResult result = validator.validate(protobufMessage);

                        if (!result.isSuccess()) {
                            BadRequest.Builder badRequestBuilder = BadRequest.newBuilder();
                            for (Violation violation : result.toProto().getViolationsList()) {
                                badRequestBuilder.addFieldViolations(
                                        BadRequest.FieldViolation.newBuilder()
                                                .setField(violation.getField().toString())
                                                .setDescription(violation.getMessage())
                                                .build()
                                );
                            }

                            closeWithValidationError(call, Code.INVALID_ARGUMENT,
                                    "Ошибка валидации входных параметров", Any.pack(badRequestBuilder.build()));
                            return;
                        }

                    } catch (build.buf.protovalidate.exceptions.ValidationException e) {
                        closeWithError(call, Code.INTERNAL, "Внутренняя ошибка проверки контракта");
                        return;
                    }
                }
                if (delegate == null) {
                    delegate = next.startCall(call, headers);
                }
                delegate.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                if (closed)
                    return;
                if (delegate != null)
                    delegate.onHalfClose();
            }

            @Override
            public void onCancel() {
                if (delegate != null)
                    delegate.onCancel();
            }

            @Override
            public void onComplete() {
                if (delegate != null)
                    delegate.onComplete();
            }

            @Override
            public void onReady() {
                if (delegate != null) {
                    delegate.onReady();
                } else {
                    call.request(1);
                }
            }

            private void closeWithValidationError(ServerCall<R, T> call, Code code, String message, Any details) {
                closed = true;
                com.google.rpc.Status rpcStatus = com.google.rpc.Status.newBuilder()
                        .setCode(code.getNumber())
                        .setMessage(message)
                        .addDetails(details)
                        .build();
                StatusRuntimeException out = StatusProto.toStatusRuntimeException(rpcStatus);
                call.close(out.getStatus(), out.getTrailers());
            }

            private void closeWithError(ServerCall<R, T> call, Code code, String message) {
                closed = true;
                com.google.rpc.Status rpcStatus = com.google.rpc.Status.newBuilder()
                        .setCode(code.getNumber())
                        .setMessage(message)
                        .build();
                StatusRuntimeException out = StatusProto.toStatusRuntimeException(rpcStatus);
                call.close(out.getStatus(), out.getTrailers());
            }
        };
    }

}
