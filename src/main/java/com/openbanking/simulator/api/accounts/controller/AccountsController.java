package com.openbanking.simulator.api.accounts.controller;

import com.openbanking.simulator.api.accounts.dto.AccountRequestDTO;
import com.openbanking.simulator.api.accounts.service.AccountsService;
import com.openbanking.simulator.api.spec.AccountsApi;
import com.openbanking.simulator.api.spec.model.ResponseBankingAccountById;
import com.openbanking.simulator.api.spec.model.ResponseBankingAccountList;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class AccountsController implements AccountsApi {

    private AccountsService accountsService;

    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @Override
    public Mono<ResponseEntity<ResponseBankingAccountList>> listAccounts(@ApiParam(value = "Version of the API end point requested by the client. Must be set to a positive integer. The data holder should respond with the highest supported version between [x-min-v](#request-headers) and [x-v](#request-headers). If the value of [x-min-v](#request-headers) is equal to or higher than the value of [x-v](#request-headers) then the [x-min-v](#request-headers) header should be treated as absent. If all versions requested are not supported then the data holder must respond with a 406 Not Acceptable. See [HTTP Headers](#request-headers)", required = true) @RequestHeader(value = "x-v", required = true) String xV, @ApiParam(value = "Used to filter results on the productCategory field applicable to accounts. Any one of the valid values for this field can be supplied. If absent then all accounts returned.", allowableValues = "BUSINESS_LOANS, CRED_AND_CHRG_CARDS, LEASES, MARGIN_LOANS, OVERDRAFTS, PERS_LOANS, REGULATED_TRUST_ACCOUNTS, RESIDENTIAL_MORTGAGES, TERM_DEPOSITS, TRADE_FINANCE, TRAVEL_CARDS, TRANS_AND_SAVINGS_ACCOUNTS") @Valid @RequestParam(value = "product-category", required = false) Optional<String> productCategory, @ApiParam(value = "Used to filter results according to open/closed status. Values can be OPEN, CLOSED or ALL. If absent then ALL is assumed", allowableValues = "OPEN, CLOSED, ALL", defaultValue = "ALL") @Valid @RequestParam(value = "open-status", required = false, defaultValue = "ALL") Optional<String> openStatus, @ApiParam("Filters accounts based on whether they are owned by the authorised customer.  True for owned accounts, false for unowned accounts and absent for all accounts") @Valid @RequestParam(value = "is-owned", required = false) Optional<Boolean> isOwned, @ApiParam(value = "Page of results to request (standard pagination)", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Optional<Integer> page, @ApiParam(value = "Page size to request. Default is 25 (standard pagination)", defaultValue = "25") @Valid @RequestParam(value = "page-size", required = false, defaultValue = "25") Optional<Integer> pageSize, @ApiParam("Minimum version of the API end point requested by the client. Must be set to a positive integer if provided. The data holder should respond with the highest supported version between [x-min-v](#request-headers) and [x-v](#request-headers). If all versions requested are not supported then the data holder must respond with a 406 Not Acceptable.") @RequestHeader(value = "x-min-v", required = false) Optional<String> xMinV, @ApiParam("An [RFC4122](https://tools.ietf.org/html/rfc4122) UUID used as a correlation id. If provided, the data holder must play back this value in the x-fapi-interaction-id response header. If not provided a [RFC4122] UUID value is required to be provided in the response header to track the interaction.") @RequestHeader(value = "x-fapi-interaction-id", required = false) Optional<String> xFapiInteractionId, @ApiParam("The time when the customer last logged in to the data recipient. Required for all resource calls (customer present and unattended). Not to be included for unauthenticated calls.") @RequestHeader(value = "x-fapi-auth-date", required = false) Optional<String> xFapiAuthDate, @ApiParam("The customer's original IP address if the customer is currently logged in to the data recipient. The presence of this header indicates that the API is being called in a customer present context. Not to be included for unauthenticated calls.") @RequestHeader(value = "x-fapi-customer-ip-address", required = false) Optional<String> xFapiCustomerIpAddress, @ApiParam("The customer's original standard http headers [Base64](#common-field-types) encoded, including the original User Agent header, if the customer is currently logged in to the data recipient. Mandatory for customer present calls.  Not required for unattended or unauthenticated calls.") @RequestHeader(value = "x-cds-client-headers", required = false) Optional<String> xCdsClientHeaders, ServerWebExchange exchange) {

        AccountRequestDTO requestDTO = new AccountRequestDTO();

        productCategory.ifPresent(value -> requestDTO.setProductCategory(value));
        openStatus.ifPresent(value -> requestDTO.setOpenStatus(value));
        isOwned.ifPresent(value -> requestDTO.setOwned(value));
        page.ifPresentOrElse(value -> requestDTO.setPage(value), () -> requestDTO.setPage(1));
        pageSize.ifPresentOrElse(value -> requestDTO.setPageSize(value), () -> requestDTO.setPageSize(25));

        Mono<ResponseBankingAccountList> accountsListResponse = accountsService.getAccounts(requestDTO);

        return accountsListResponse.flatMap(response -> {

            System.out.println("===> Controller :: flatmap :: ");
            System.out.println(response);

            return Mono.just(response);
        })
        .map(response -> ResponseEntity.of(Optional.of(response)));
    }

    @Override
    public Mono<ResponseEntity<ResponseBankingAccountById>> getAccountDetail(@ApiParam(value = "A tokenised identifier for the account which is unique but not shareable", required = true) @PathVariable("accountId") String accountId, @ApiParam(value = "Version of the API end point requested by the client. Must be set to a positive integer. The data holder should respond with the highest supported version between [x-min-v](#request-headers) and [x-v](#request-headers). If the value of [x-min-v](#request-headers) is equal to or higher than the value of [x-v](#request-headers) then the [x-min-v](#request-headers) header should be treated as absent. If all versions requested are not supported then the data holder must respond with a 406 Not Acceptable. See [HTTP Headers](#request-headers)", required = true) @RequestHeader(value = "x-v", required = true) String xV, @ApiParam("Minimum version of the API end point requested by the client. Must be set to a positive integer if provided. The data holder should respond with the highest supported version between [x-min-v](#request-headers) and [x-v](#request-headers). If all versions requested are not supported then the data holder must respond with a 406 Not Acceptable.") @RequestHeader(value = "x-min-v", required = false) Optional<String> xMinV, @ApiParam("An [RFC4122](https://tools.ietf.org/html/rfc4122) UUID used as a correlation id. If provided, the data holder must play back this value in the x-fapi-interaction-id response header. If not provided a [RFC4122] UUID value is required to be provided in the response header to track the interaction.") @RequestHeader(value = "x-fapi-interaction-id", required = false) Optional<String> xFapiInteractionId, @ApiParam("The time when the customer last logged in to the data recipient. Required for all resource calls (customer present and unattended). Not to be included for unauthenticated calls.") @RequestHeader(value = "x-fapi-auth-date", required = false) Optional<String> xFapiAuthDate, @ApiParam("The customer's original IP address if the customer is currently logged in to the data recipient. The presence of this header indicates that the API is being called in a customer present context. Not to be included for unauthenticated calls.") @RequestHeader(value = "x-fapi-customer-ip-address", required = false) Optional<String> xFapiCustomerIpAddress, @ApiParam("The customer's original standard http headers [Base64](#common-field-types) encoded, including the original User Agent header, if the customer is currently logged in to the data recipient. Mandatory for customer present calls.  Not required for unattended or unauthenticated calls.") @RequestHeader(value = "x-cds-client-headers", required = false) Optional<String> xCdsClientHeaders, ServerWebExchange exchange) {
        return null;
    }
 }
