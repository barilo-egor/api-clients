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
import tgb.cryptoexchange.apiclients.exceptions.GrpcBaseException;

@Slf4j
@Component
@GlobalServerInterceptor
public class GrpcValidationInterceptor implements ServerInterceptor {

    Validator validator = ValidatorFactory.newBuilder().build();

    @Override
    public <R, T> ServerCall.Listener<R> interceptCall(
            ServerCall<R, T> call, Metadata headers, ServerCallHandler<R, T> next) {

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<R>(
                next.startCall(call, headers)) {
            @Override
            public void onMessage(R message) {
                if (message instanceof Message protobufMessage) {
                    try {
                        ValidationResult result = validator.validate(protobufMessage);

                        if (!result.isSuccess()) {
                            BadRequest.Builder badRequestBuilder = BadRequest.newBuilder();
                            for (Violation violation : result.toProto().getViolationsList()) {
                                String fieldPath = violation.getField().toString();
                                String errorMessage = violation.getMessage();

                                BadRequest.FieldViolation fieldViolation = BadRequest.FieldViolation.newBuilder()
                                        .setField(fieldPath)
                                        .setDescription(errorMessage)
                                        .build();

                                badRequestBuilder.addFieldViolations(fieldViolation);
                            }
                            Any anyDetails = Any.pack(badRequestBuilder.build());
                            throw new GrpcBaseException(
                                    Code.INVALID_ARGUMENT,
                                    "Ошибка валидации входных параметров",
                                    anyDetails
                            );
                        }
                        super.onMessage(message);
                    } catch (GrpcBaseException grpcEx) {
                        StatusRuntimeException out = StatusProto.toStatusRuntimeException(grpcEx.getRpcStatus());
                        call.close(out.getStatus(), out.getTrailers());
                    } catch (build.buf.protovalidate.exceptions.ValidationException e) {
                        com.google.rpc.Status rpcStatus = com.google.rpc.Status.newBuilder()
                                .setCode(Code.INTERNAL_VALUE)
                                .setMessage("Внутренняя ошибка проверки контракта")
                                .build();
                        StatusRuntimeException runtimeEx = io.grpc.protobuf.StatusProto.toStatusRuntimeException(
                                rpcStatus);
                        call.close(runtimeEx.getStatus(), runtimeEx.getTrailers());
                    }
                } else {
                    super.onMessage(message);
                }
            }
        };
    }

}
