import lxml.etree as etree
import sys

def pp(filename):
   x = etree.parse(f)
   print etree.tostring(x, pretty_print = True)

if __name__ == '__main__':
  f = sys.argv[1]
  pp(f)
