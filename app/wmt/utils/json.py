
def load_mapping(filename):
    import json

    with open(filename, 'r') as map:
        mapping = json.loads(map.read())

    if not isinstance(mapping, dict):
        raise ValueError(filename)

    return mapping
