package com.qkl.wallet.service;

import com.qkl.wallet.domain.ConfirmListenerEntity;
import com.qkl.wallet.domain.TransactionListenerEvent;

public interface HubService {
    Boolean submitTransferEvent(TransactionListenerEvent transactionListenerEvent);

    Boolean confirmBlockNumEvent(ConfirmListenerEntity confirmListenerEntity);
}
