from ..installer.components import download_and_unpack_from_github


def main():
    import argparse
    parser = argparse.ArgumentParser()

    parser.add_argument('--dest', default='components',
                        help='unpack database here')

    args = parser.parse_args()

    download_and_unpack_from_github('csdms/component_metadata', dest=args.dest)
