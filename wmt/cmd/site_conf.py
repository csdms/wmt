from ..installer.site import Site
from .site_setup import read_site_vars, parse_args, collect_user_vars


def main():
    args = parse_args()

    user_vars = read_site_vars(args.file)
    user_vars.update(collect_user_vars(args.conf))
    user_vars.update([('name', args.name),
                      ('email', args.email),
                      ('url_scheme', args.url_scheme),
                      ('url_netloc', args.url_netloc),
                      ('url_path', args.url_path),
                      ('pickup_scheme', args.pickup_scheme),
                      ('pickup_netloc', args.pickup_netloc),
                      ('pickup_path', args.pickup_path),
                     ])

    #if not args.dry_run:
    #    setup(args.prefix, user_vars)
    
    site = Site(args.prefix, options=user_vars)
    print site.to_conf_string()

    #print_post_setup_instructions(args)
