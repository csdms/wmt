import os
import urllib2
import zipfile


def download_components(url):
    dest = os.path.basename(url)
    src_fp = urllib2.urlopen(url)
    with open(dest, 'w') as dest_fp:
        dest_fp.write(src_fp.read())
    return dest


def unpack_components(filename):
    zip = zipfile.ZipFile(filename, mode='r')
    files = zip.namelist()
    prefix = os.path.commonprefix(files)
    zip.extractall()
    os.rename(prefix, 'components')


def main():
    package = download_components('https://github.com/csdms/component_metadata/archive/master.zip')
    unpack_components(package)


if __name__ == '__main__':
    main()
