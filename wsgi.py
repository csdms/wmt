from wmt.flask import create_app


if __name__ == "__main__":
    from os import path

    create_app(wmt_root_path=path.dirname(__file__)).run()
