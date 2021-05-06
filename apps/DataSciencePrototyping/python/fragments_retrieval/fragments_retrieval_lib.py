'''
"""TODO(dzmr): Add a docstring."""

from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler
from parser_lib_3 import HierarchicalParser, HeadersParser
from text_processing_lib import generate_hc_features, get_npt_responsibility
from utilities import set_params

import pickle

LABELS_MAP = {1 : "FORCE MAJEURE", 2: "RIG REPAIR"}

class FragmentsRetrievalModel():
    """TODO(dzmr): Add a docstring."""

    def __init__(self, ml_model_path="", params={}):
        self.params = {}
        self.hierarchical_parser = HierarchicalParser()
        self.headers_parser = HeadersParser()
        self.classifier = pickle.load(open(ml_model_path, 'rb'))
        set_params(self.params, params)

    def get_relevant_sections(self, document_path="", text=""):
        def _get_candidates_with_predictions(candidates):
            if len(candidates) == 0:
                return []
            X = generate_hc_features([text[l:r] for l, r in candidates])
            predicted = self.classifier.predict(X)
            results = []
            for index, label in enumerate(predicted):
                if label == 0:
                    continue
                results.append((candidates[index], label))
            return results

        hierarchical_candidates = self.hierarchical_parser.parse(text)
        headers_candidates = self.headers_parser.parse(text)

        hierarchical_candidates_ = _get_candidates_with_predictions(hierarchical_candidates)
        headers_candidates_ = _get_candidates_with_predictions(headers_candidates)

        hierarchical_candidates = []
        for i, ((l, r), label) in enumerate(hierarchical_candidates_):
            sub_candidate = False
            for j, ((l_, r_), _) in enumerate(hierarchical_candidates_):
                if i != j and l_ <= l and r <= r_:
                    sub_candidate = True
                    break
            if sub_candidate:
                continue
            hierarchical_candidates.append(((l, r), label))

        headers_candidates = []
        for i, ((l, r), label) in enumerate(headers_candidates_):
            sub_candidate = False
            for j, ((l_, r_), _) in enumerate(hierarchical_candidates):
                if r < l_ or r_ < l:
                    continue
                sub_candidate = True
                break
            if sub_candidate:
                continue
            headers_candidates.append(((l, r), label))

        results = []
        for (l, r), label in hierarchical_candidates:
            results.append((text[l:r], LABELS_MAP[label]))
        for (l, r), label in headers_candidates:
            results.append((text[l:r], LABELS_MAP[label]))

        # NPT responsibility filtering phase.
        npt_resp_results = []
        for text, label in results:
            npt_responsibility = get_npt_responsibility(text)
            if npt_responsibility == True:
                npt_resp_results.append((text, label))
        return npt_resp_results
'''