from flask import jsonify


DOCUMENTATION_URL = "https://developer.github.com/v3"


class Error(Exception):
    status_code = 400
    message = "Bad request"
    def __str__(self):
        return self.message

    def to_resource(self):
        return {
            "message": str(self),
            "documentation_url": DOCUMENTATION_URL
        }

    def jsonify(self):
        resp = jsonify(self.to_resource())
        resp.status_code = self.status_code
        return resp


class AuthenticationError(Error):
    status_code = 401
    message = "Bad credentials"


class AuthorizationError(Error):
    status_code = 403
    message = "Not allowed"


class InvalidJsonError(Error):
    status_code = 400
    message = "Problems parsing JSON"


class WrongJsonError(Error):
    status_code = 400
    message = "Body should be a JSON object"


class ProcessingError(Error):
    status_code = 422
    message = "Validation failed"
    def __init__(self, resource, field, code):
        self.resource = resource
        self.field = field
        self.code = code

    def to_resource(self):
        resource = super(ProcessingError, self).to_resource()
        resource["errors"] = [{
            "resource": self.resource,
            "field": self.field,
            "code": self.code
        }]
        return resource


class AlreadyExistsError(ProcessingError):
    def __init__(self, resource, field):
        super(AlreadyExistsError, self).__init__(resource, field,
                                                 "already_exists")


class MissingFieldError(ProcessingError):
    def __init__(self, resource, field):
        super(MissingFieldError, self).__init__(resource, field,
                                                "missing_field")


def jsonify_error(error):
    return error.jsonify()


ERROR_HANDLERS = [
    (AuthenticationError, jsonify_error),
    (AuthorizationError, jsonify_error),
    (InvalidJsonError, jsonify_error),
    (WrongJsonError, jsonify_error),
    (AlreadyExistsError, jsonify_error),
    (MissingFieldError, jsonify_error),
]
