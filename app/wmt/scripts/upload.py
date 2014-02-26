from wmt.utils.io import upload_large_file


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('file', help='File to upload')

    args = parser.parse_args()

    print upload_large_file(args.file, 'http://csdms.colorado.edu/wmt/run/upload').text
