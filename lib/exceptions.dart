class QuickPaySetupException implements Exception {
  final String message;

  QuickPaySetupException(this.message);

  @override
  String toString() {
    return 'QuickPaySetupException: $message';
  }
}

class CreatePaymentException implements Exception {
  final String message;

  CreatePaymentException(this.message);

  @override
  String toString() {
    return 'CreatePaymentException: $message';
  }
}

class CreatePaymentLinkException implements Exception {
  final String message;

  CreatePaymentLinkException(this.message);

  @override
  String toString() {
    return 'CreatePaymentLinkException: $message';
  }
}

class ActivityException implements Exception {
  final String message;

  ActivityException(this.message);

  @override
  String toString() {
    return 'ActivityException: $message';
  }
}

class ActivityFailureException implements Exception {
  final String message;

  ActivityFailureException(this.message);

  @override
  String toString() {
    return 'ActivityFailureException: $message';
  }
}

class PaymentFailureException implements Exception {
  final String message;

  PaymentFailureException(this.message);

  @override
  String toString() {
    return 'PaymentFailureException: $message';
  }
}
