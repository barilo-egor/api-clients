package tgb.cryptoexchange.apiclients.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.apiclients.dto.WithdrawalRequestDTO;
import tgb.cryptoexchange.apiclients.entity.WithdrawalRequest;
import tgb.cryptoexchange.apiclients.exceptions.FieldNotBeEmptyException;
import tgb.cryptoexchange.apiclients.exceptions.NotFoundException;
import tgb.cryptoexchange.apiclients.mapper.WithdrawalMapper;
import tgb.cryptoexchange.apiclients.repository.WithdrawalRequestRepository;

@Service
@Slf4j
@Transactional
public class WithdrawalRequestService {

    private final WithdrawalRequestRepository withdrawalRequestRepository;

    private final WithdrawalMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    public WithdrawalRequestService(WithdrawalRequestRepository withdrawalRequestRepository,
                                    WithdrawalMapper mapper, @Autowired(required = false) ApplicationEventPublisher eventPublisher) {
        this.withdrawalRequestRepository = withdrawalRequestRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    public void saveWithdrawalRequest(WithdrawalRequestDTO withdrawalRequest) {
        WithdrawalRequest saved = withdrawalRequestRepository.save(mapper.requestDTOToEntity(withdrawalRequest));
        WithdrawalRequestDTO dto = mapper.entityToDTO(saved);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(dto);
        }
    }

    public void updateWithdrawalRequest(Long id, WithdrawalRequestDTO withdrawalRequestDTO) {
        if (id == null) {
            throw new FieldNotBeEmptyException("id");
        }
        WithdrawalRequest withdrawalRequest = withdrawalRequestRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.valueOf(id))
        );
        withdrawalRequest.setWallet(withdrawalRequestDTO.getWallet());
        withdrawalRequest.setComment(withdrawalRequestDTO.getComment());
    }


}
