class Payment {
  final int id;
  final String orderId;
  final bool accepted;
  final String type;
  final String textOnStatement;
  final String currency;
  final String state;
  final bool testMode;
  final String createdAt;
  final String updatedAt;
  final int balance;
  final String brandingId;
  final String acquirer;
  final String facilitator;
  final String retentedAt;
  final int fee;
  final int subscriptionId;
  final String deadlineAt;
  final MetaData metaData;
  final List<Operation> operations;

  Payment({
    this.id,
    this.orderId,
    this.accepted,
    this.type,
    this.textOnStatement,
    this.currency,
    this.state,
    this.testMode,
    this.createdAt,
    this.updatedAt,
    this.balance,
    this.brandingId,
    this.acquirer,
    this.facilitator,
    this.retentedAt,
    this.fee,
    this.subscriptionId,
    this.deadlineAt,
    this.metaData,
    this.operations,
  });

  factory Payment.fromMap(Map map) {
    return Payment(
      id: map['id'] as int,
      orderId: map['order_id'] as String,
      accepted: map['accepted'] as bool,
      type: map['type'] as String,
      textOnStatement: map['text_on_statement'] as String,
      currency: map['currency'] as String,
      state: map['state'] as String,
      testMode: map['test_mode'] as bool,
      createdAt: map['created_at'] as String,
      updatedAt: map['updated_at'] as String,
      balance: map['balance'] as int,
      brandingId: map['branding_id'] as String,
      acquirer: map['acquirer'] as String,
      facilitator: map['facilitator'] as String,
      retentedAt: map['retented_at'] as String,
      fee: map['fee'] as int,
      subscriptionId: map['subscriptionId'] as int,
      deadlineAt: map['deadline_at'] as String,
      metaData: MetaData.fromMap(map['metadata']),
      operations: (map['operatinons'] as List<dynamic>).map((entry) => Operation.fromMap(entry)).toList(),
    );
  }
}

class MetaData {
  final String type;
  final String origin;
  final String brand;
  final String bin;
  final bool corporate;
  final String last4;
  final int expMonth;
  final int expYear;
  final String country;
  final bool is3dSecure;
  final String issuedTo;
  final String hash;
  final String number;
  final String customerIp;
  final String customerCountry;
  final String shopSystemName;
  final String shopSystemVersion;

  MetaData({
    this.type,
    this.origin,
    this.brand,
    this.bin,
    this.corporate,
    this.last4,
    this.expMonth,
    this.expYear,
    this.country,
    this.is3dSecure,
    this.issuedTo,
    this.hash,
    this.number,
    this.customerIp,
    this.customerCountry,
    this.shopSystemName,
    this.shopSystemVersion,
  });

  factory MetaData.fromMap(Map map) {
    return MetaData(
      type: map['type'] as String,
      origin: map['origin'] as String,
      brand: map['brand'] as String,
      bin: map['bin'] as String,
      corporate: map['corporate'] as bool,
      last4: map['last4'] as String,
      expMonth: map['exp_month'] as int,
      expYear: map['exp_year'] as int,
      country: map['country'] as String,
      is3dSecure: map['is_3d_secure'] as bool,
      issuedTo: map['issued_to'] as String,
      hash: map['hash'] as String,
      number: map['number'] as String,
      customerIp: map['customer_ip'] as String,
      customerCountry: map['customer_country'] as String,
      shopSystemName: map['shopsystem_name'] as String,
      shopSystemVersion: map['shopsystem_version'] as String,
    );
  }
}

class Operation {
  final int id;
  final String type;
  final int amount;
  final bool pending;
  final String qpStatusCode;
  final String qpStatusMsg;
  final String aqStatusMsg;
  final String acquirer;

  Operation({
    this.id,
    this.type,
    this.amount,
    this.pending,
    this.qpStatusCode,
    this.qpStatusMsg,
    this.aqStatusMsg,
    this.acquirer,
  });

  factory Operation.fromMap(Map map) {
    return Operation(
      id: map['id'] as int,
      type: map['type'] as String,
      amount: map['amount'] as int,
      pending: map['pending'] as bool,
      qpStatusCode: map['qp_status_code'] as String,
      qpStatusMsg: map['qp_status_msg'] as String,
      aqStatusMsg: map['aq_status_msg'] as String,
      acquirer: map['acquirer'] as String,
    );
  }
}
