from ..utils.json import load_component


def validate(path_to_file):
    try:
        desc = load_component(path_to_file)
    except ValueError as error:
        raise error

def main():
    import argparse

    parser = argparse.ArgumentParser(description='validate a component JSON')
    parser.add_argument('file', type=str, help='JSON to validate')

    args = parser.parse_args()

    try:
        validate(args.file)
    except ValueError as error:
        print str(error)
