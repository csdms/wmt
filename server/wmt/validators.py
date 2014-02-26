import web

from .cca.json import check_json_is_valid


def not_too_short(min_len):
    return web.form.Validator("Must be more than %d characters" % min_len,
                              lambda text: len(text) > min_len)


def not_too_long(max_len):
    return web.form.Validator("Must be less than %d characters" % max_len,
                              lambda text: len(text) < max_len)


not_bad_json = web.form.Validator(
    """Invalid JSON (<a href="/wmt/help/new">What's valid?</a>)""",
    check_json_is_valid)


valid_email_address = web.form.regexp(
    '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$', 'Invalid email')
