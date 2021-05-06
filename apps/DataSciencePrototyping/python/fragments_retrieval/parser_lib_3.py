'''
from __future__ import unicode_literals
from __future__ import print_function
import logging
import time
timestr = time.strftime("%Y%m%d-%H%M%S")
print("Program Parser_lib_3.py started at %s"%(timestr))
logpath = r"/home/Desktop/Fragments_Retrieval_2to3/Fragments_Retrieval_2to3/logs/"
logname = logpath+'fragments_retrieval_'+timestr+'.log'
logging.basicConfig(filename=logname,
                            filemode='w',
                            format='%(asctime)s %(lineno)d %(name)s %(funcName)s %(levelname)s %(message)s',level=logging.INFO) 

from config import HIERARCHICAL_PARSER_PARAMS, HEADERS_PARSER_PARAMS
from utilities import get_next_marker, get_new_subsection_marker, \
                      is_valid_markup_element, set_params, ROMAN_NUMERALS_MAP
#from builtins import bytes

class Node():
    def __init__(self, start_index=0, end_index=-1, heading_length=1):
        self.l = start_index
        self.r = end_index
        self.heading_length = heading_length
        self.child_nodes = []

class Hierarchy():
    def __init__(self, root_node, text, params={}):
        self.params = {}
        set_params(self.params, params)

        self.root_node = root_node
        self.text = text

    def generate_candidates(self):
        def _get_nodes_marker(node):
            #print(self.text[node.l:node.l+15].split())
            if(len(self.text[node.l:node.l+15].split()) == 0):
                #print("0 text")
                return " "
            return self.text[node.l:node.l+15].split()[0]

        def _is_subsection(marker):
            #print(marker)
            content = str(marker).split('.')
            if len(content) == 2 and all([x.isdigit() for x in content]):
                return True
            return False

        def _generate_candidates(node, candidates, height):
            if height > 3:
                return candidates
            # Option 1: only section's name.
            #if len(node.child_nodes) > 0:
            #    candidates.append((node.l, node.l+node.heading_length))
            # Option 2: the whole section content.
            if height >= 1:
                candidates.append((node.l, node.r))
            # Option 3: retrieve candidates from child nodes.
            for child_node in node.child_nodes:
                candidates = _generate_candidates(child_node, candidates, height+1)
            # Option 4: group child nodes by section number:
            # 12.1, 12.2, 12.3 -> 12---[12.1, 12.2, 12.3].
            left_index = 0
            while left_index < len(node.child_nodes):
                cur_marker = _get_nodes_marker(node.child_nodes[left_index])
                if not _is_subsection(cur_marker):
                    left_index += 1
                    continue
                right_index = left_index+1
                while right_index < len(node.child_nodes):
                    marker_ = _get_nodes_marker(node.child_nodes[right_index])
                    if not _is_subsection(cur_marker):
                        break
                    if cur_marker.split(".")[0] != marker_.split(".")[0]:
                        break
                    right_index += 1
                if right_index - left_index > 1 and height <= 2:
                    candidates.append((node.child_nodes[left_index].l,
                                       node.child_nodes[right_index-1].r))
                left_index = right_index
            return candidates

        candidates = []
        return _generate_candidates(self.root_node, candidates, 0)

    def print_structure(self):
        def _dfs(node, prefix):
            print(prefix + self.text[node.l:node.l+node.heading_length] + "\n")
            for child_node in node.child_nodes:
                _dfs(child_node, prefix+"*"*8)

        _dfs(self.root_node, "")

    def to_dict(self):
        def _build_dict(node, dict_):
            node_name = self.text[node.l:node.l+20]#min(node.heading_length, 15)]
            dict_["name"] = node_name
            if len(node.child_nodes) > 0:
                dict_["_children"] = []
            for child_node in node.child_nodes:
                child_dict = {"parent": node_name}
                dict_["_children"].append(child_dict)
                _build_dict(child_node, child_dict)

        result = {"parent": "null"}
        _build_dict(self.root_node, result)
        return result

class HierarchicalParser():
    def __init__(self, params={}):
        self.params = {}
        set_params(self.params, HIERARCHICAL_PARSER_PARAMS)
        set_params(self.params, params)

    def _build_skeleton(self, flat_structure, indexes, skeleton, height=0):
        def _remove_mistakes(name):
            return name.replace("l", "1").replace("S", "5").replace("l", "1")
        cur_index = 0
        while cur_index < len(indexes):
            sub_skeleton = [indexes[cur_index]]
            skeleton.append(sub_skeleton)
            current_section_name = _remove_mistakes(flat_structure[indexes[cur_index]][0])

            if height == 0:
                if current_section_name[0] == "(" or \
                        ("." in current_section_name and current_section_name[-1] != "."):
                    cur_index = cur_index + 1
                    continue
            # Based on markup element we have currently, we can try to find
            # another element that follows the current one in actual
            # markup structure.
            next_section_name = get_next_marker(current_section_name)
            new_subsection_name = get_new_subsection_marker(
                current_section_name)
            next_index = -1
            # Looking for a match.
            for index in range(cur_index+1, len(indexes)):
                if index - cur_index > self.params["markup_max_subelements"]:
                    break
                section_name = _remove_mistakes(flat_structure[indexes[index]][0])
                if section_name == current_section_name and index - cur_index > 40:
                    break
                if section_name == next_section_name or \
                        section_name == new_subsection_name:
                    # Match founded, so we are trying to retrieve structure
                    # within all elements between, also we suppose they are
                    # nested.
                    self._build_skeleton(
                        flat_structure, indexes[cur_index + 1 : index], sub_skeleton, height+1)
                    next_index = index
                    break
            if next_index != -1:
                cur_index = next_index
            else:
                cur_index = cur_index + 1

    def markup_based_hierarchy(self, text, verbose=0):
        def _build_hierarchy(node, flat_structure, skeleton):
            if len(skeleton) == 0:
                return -1
            cur_index = node.l + node.heading_length
            for sub_skeleton in skeleton:
                section_id = sub_skeleton[0]
                section_heading_length = sum([
                    len(x) + int(len(x) > 0)
                    for x in flat_structure[section_id]
                ])
                # New child node.
                new_node = Node(cur_index, -1, section_heading_length)
                if len(sub_skeleton) > 1:
                    new_node.r = _build_hierarchy(
                        new_node, flat_structure, sub_skeleton[1:])
                else:
                    new_node.r = cur_index + section_heading_length
                node.child_nodes.append(new_node)
                cur_index = new_node.r
            return node.child_nodes[-1].r

        # # Remove Roman numerals.
        # for roman_n, latin_n in sorted(ROMAN_NUMERALS_MAP.items(),
        #                                key=lambda x: len(x[0]),
        #                                reverse=True):
        #     text = text.replace(" {} ".format(roman_n),
        #                         " {} ".format(latin_n))

        # Retrieve markup elements from text.
        terms = [[]]
        text_split = text.split()
        for index, term in enumerate(text_split):
            if is_valid_markup_element(term,
                                       prev_term=text_split[index-1:index],
                                       next_term=text_split[index+1:index+2]):
                terms.append([term])
            else:
                terms[-1].append(term)
        if verbose:
            print([x[0] for x in terms])
        # Sample flat hierarchy.
        root_node = Node(0, -1)
        text_heading = " ".join(str(terms[0]))
        flat_structure = [(section_terms[0], " ".join(section_terms[1:]))
                          for section_terms in str(terms[1:])]
        # print "\n\n\n".join(["{}\n{}".format(x,y.strip()) for x,y in flat_structure])
        # Looking for a structure within markup elements.
        heirarchy_skeleton = []
        self._build_skeleton(
            flat_structure, range(len(flat_structure)), heirarchy_skeleton, height=0)
        # Build hierarchy based on the skeleton.
        root_node = Node(0, -1, len(text_heading) + 1)
        _build_hierarchy(root_node, flat_structure, heirarchy_skeleton)
        return Hierarchy(root_node, text)

    def parse(self, text, verbose=0):
        # Generate hierarchies.
        hierarchies = [self.markup_based_hierarchy(text, verbose=verbose)]
        # Generate candidates.
        candidates = []
        for hierarchy in hierarchies:
            candidates += hierarchy.generate_candidates()
        # Remove duplicates.
        distinct_candidates = list(set(candidates))
        # Filter candidates by length.
        candidates = sorted([
                      (begin_, end_)
                      for begin_, end_ in distinct_candidates
                      if end_ - begin_ < 5500 and end_ - begin_ > 80])
        return candidates

class HeadersParser():
    def __init__(self, params={}):
        self.params = {}
        set_params(self.params, HEADERS_PARSER_PARAMS)
        set_params(self.params, params)

    def split_by_headers(self, text, verbose=0):
        root_node = Node(0, -1)
        # Contains a list of all possible split indexes.
        split_indexes = []
        # Trying to split by headers.
        patterns = self.params["headers_separators"]
        for p_index, pattern in enumerate(patterns):
  
            splits = text.split(pattern.encode())
            if len(splits) == 1 and \
                    len(splits) > self.params["headers_max_occerrences"]:
                continue
            cur_index = 0
            for section in splits[:-1]:
                cur_index += len(section)
                split_indexes.append((cur_index, p_index))
                cur_index += len(pattern)
        result_sections = []
        last_index = 0
        # Evaluate results and select the result sections using "greedy"
        # approach.
        for index, p_index in sorted(split_indexes):
            if index - last_index > self.params["headers_min_section_length"]:
                result_sections.append((last_index, index))
                last_index = index
        result_sections.append((last_index, -1))
        # Build the result hierarchy.
        if len(result_sections) > 1:
            for section_start, section_end in result_sections:
                root_node.child_nodes.append(Node(section_start, section_end))
        return Hierarchy(root_node, text)

    def parse(self, text, verbose=0):
        # Generate hierarchies.
        hierarchies = [self.split_by_headers(text, verbose=verbose)]
        # Generate candidates.
        candidates = []
        for hierarchy in hierarchies:
            candidates += hierarchy.generate_candidates()
        # Remove duplicates.
        distinct_candidates = list(set(candidates))
        # Filter candidates by length.
        candidates = sorted([
                      (begin_, end_)
                      for begin_, end_ in distinct_candidates
                      if end_ - begin_ < 5500 and end_ - begin_ > 80])
        return candidates
'''