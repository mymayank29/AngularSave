'''
#from __future__ import unicode_literals
#from __future__ import print_function
import logging
import time
timestr = time.strftime("%Y%m%d-%H%M%S")
print("Program utilies.py started at %s"%(timestr))
logpath = r"/home/hqej/Desktop/Fragments_Retrieval_2to3/Fragments_Retrieval_2to3/logs/"
logname = logpath+'fragments_retrieval_'+timestr+'.log'
logging.basicConfig(filename=logname,
                            filemode='w',
                            format='%(asctime)s %(lineno)d %(name)s %(funcName)s %(levelname)s %(message)s',level=logging.INFO)


"""TODO(dzmr): Add a docstring."""


ROMAN_NUMBERS = ("i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x")
ROMAN_NUMERALS_MAP = {"I":1, "II":2, "III":3, "IV":4, "V":5, "VI":6, "VII":7,
                      "VIII":8, "IX":9, "X":10, "XI":11, "XII":12, "XIII":13,
                      "XIV":14, "XV":15, "XVI":16, "XVII":17, "XVIII":18,
                      "XIX":19, "XX":20}

def set_params(params, new_params):
    for key in new_params:
        params[key] = new_params[key]

def remove_ocr_mistakes(x):
    return str(x).replace("l", "1").replace("S", "5").replace("I", "1")


logging.info("Removing OCR mistakes based on present word, previous word and next term...")
def is_valid_markup_element(x, prev_term=[], next_term=[]):
   
    if x == "":
        return False
    x = remove_ocr_mistakes(x)


    if str(x).isdigit():
        if len(next_term) > 0 and next_term[0][0].isupper() and \
           len(prev_term) > 0 and prev_term[0].isupper():
            return True
    if x[-1] == "." and x[:-1].isdigit():
        if len(next_term) > 0 and not next_term[0][0].isupper():
            return False
        if len(next_term) > 0 and next_term[0].isdigit():
            return False
        if len(next_term) > 0 and next_term[0].isupper():
            return True
        if len(prev_term) > 0 and prev_term[0][-1] == ".":
            return True
        if len(prev_term) > 0 and prev_term[0].isupper():
            return True
        if len(prev_term) > 0 and prev_term[0][-1].isdigit():
            return True
    if len(x) <= 5 and x[0] == "(" and x[-1] == ")" and \
            (x[1:-1].isalpha() or x[1:-1].isdigit()):
        return True
    content = x.split(".")
    if "." in x and all([term.isdigit() and int(term) < 50 and term != ""
                         for index, term in enumerate(content)]):
        return True
    return False

def get_next_marker(marker):
    marker = remove_ocr_mistakes(marker)
    if marker.isdigit():
        return "{}".format(int(marker)+1)
    if marker[-1] == "." and marker[:-1].isdigit():
        return "{}.".format(int(marker[:-1])+1)
    if marker[0] == "(" and marker[-1] == ")":
        content = marker[1:-1]
        # i, ii, iii, iv, ...
        roman_numbers = ["i", "ii", "iii", "iv", "v", "vi", "vii", "viii",
                       "ix", "x"]
        if content in ROMAN_NUMBERS:
            return "({})".format(ROMAN_NUMBERS[ROMAN_NUMBERS.index(content)+1])
        # A, B, ..., Z, AA, BB, ...
        if len(set(content)) == 1 and content[0] >= "A" and content[0] <= "Z":
            if content[0] == "Z":
                return "({})".format("A"*(len(content)+1))
            else:
                return "({})".format(chr(ord(content[0])+1)*len(content))
        # 1, 2, 3, ...
        if content.isdigit():
            return "({})".format(int(content)+1)
        return ""
    content = marker.split(".")
    if content[-1] != "" and len(content) > 1:
        return ".".join(content[:-1])+"."+str(int(content[-1])+1)
    return ""

def get_new_subsection_marker(marker):
    marker = remove_ocr_mistakes(marker)
    if marker[0] == "(" and marker[-1] == ")":
        return ""
    content = marker.split(".")
    if content[-1] != "" and len(content) > 1:
        return ".".join(content[:-2] + [str(int(content[-2])+1)])+".1"
    return ""

    '''
