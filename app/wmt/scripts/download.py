from wmt.utils.io import download_file


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('id', help='run id to download')

    args = parser.parse_args()
    print download_file('http://csdms.colorado.edu/wmt/run/download/%s' % args.id)

