import web

from .cca.json import check_json_is_valid
from .models.submissions import get_uuids


def submission_exists():
    return web.form.Validator(
        "Unable to find submission", lambda text: text in get_uuids())

def model_exists():
    return web.form.Validator(
        "Unable to find model", lambda text: text in get_model_ids())


def not_too_short(min_len):
    return web.form.Validator("Must be more than %d characters" % min_len,
                              lambda text: len(text) > min_len)


def not_too_long(max_len):
    return web.form.Validator("Must be less than %d characters" % max_len,
                              lambda text: len(text) < max_len)


not_bad_json = web.form.Validator(
    """Invalid JSON (<a href="/wmt/help/new">What's valid?</a>)""",
    check_json_is_valid)


_UUID_REGEX = '[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}'
_EMAIL_ADDRESS_REGEX = '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$'


valid_email_address = web.form.regexp(_EMAIL_ADDRESS_REGEX, 'Invalid email')
valid_uuid = web.form.regexp(_UUID_REGEX, 'Invalid UUID')
