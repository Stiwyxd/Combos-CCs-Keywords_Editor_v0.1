import sys
import os
from collections import defaultdict
import random

def process_file_line_by_line(input_path, output_path, process_func):
    """Process file line-by-line to minimize memory usage."""
    with open(input_path, 'r', encoding='utf-8', errors='replace') as infile, \
         open(output_path, 'w', encoding='utf-8') as outfile:
        for line in infile:
            processed_line = process_func(line)
            if processed_line is not None:
                outfile.write(processed_line)

def password_remover(file_path: str, tab_type: str) -> str:
    try:
        def process(line):
            if ':' in line:
                return line.split(':', 1)[0] + '\n'
            return line
        process_file_line_by_line(file_path, 'cleared.txt', process)
        return "Passwords deleted successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def email_remover(file_path: str, tab_type: str) -> str:
    try:
        def process(line):
            if '@' not in line.split(':')[0].strip():
                return line
            return None
        process_file_line_by_line(file_path, 'cleared.txt', process)
        return "Emails removed successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def remove_duplicate(file_path: str, tab_type: str) -> str:
    try:
        seen = set()
        output_file = 'cleared.txt' if tab_type == 'combo' else f"{tab_type}_cleared.txt"
        with open(file_path, 'r', encoding='utf-8', errors='replace') as infile, \
             open(output_file, 'w', encoding='utf-8') as outfile:
            for line in infile:
                if line not in seen:
                    seen.add(line)
                    outfile.write(line)
        return "Duplicates removed successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def sort_lines(file_path: str, tab_type: str) -> str:
    try:
        lines = set()
        with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
            for line in infile:
                if line.strip():
                    lines.add(line.strip())
        output_file = 'sorted_' + ('combo.txt' if tab_type == 'combo' else f"{tab_type}.txt")
        with open(output_file, 'w', encoding='utf-8') as outfile:
            for line in sorted(lines):
                outfile.write(line + '\n')
        return "Lines sorted successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def removelines(file_path: str, tab_type: str) -> str:
    try:
        def process(line):
            if line.strip() and len(line.split(':')) == 2:
                return line
            return None
        process_file_line_by_line(file_path, 'cleared.txt', process)
        return "Bad lines removed successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def combine_combos(file_path: str, tab_type: str, second_file_path: str) -> str:
    try:
        with open('combined.txt', 'w', encoding='utf-8') as outfile:
            with open(file_path, 'r', encoding='utf-8', errors='replace') as infile1:
                outfile.writelines(infile1)
            with open(second_file_path, 'r', encoding='utf-8', errors='replace') as infile2:
                outfile.writelines(infile2)
        return "Combos combined successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def splitter(file_path: str, tab_type: str, num_parts: int) -> str:
    try:
        with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
            lines = infile.readlines()
            total_lines = len(lines)
            lines_per_part = total_lines // num_parts
            remainder = total_lines % num_parts

            for i in range(num_parts):
                start_idx = i * lines_per_part
                end_idx = start_idx + lines_per_part + (1 if i < remainder else 0)
                with open(f'split_{i}_{os.path.basename(file_path)}', 'w', encoding='utf-8') as outfile:
                    outfile.writelines(lines[start_idx:end_idx])
        return f"Combos split into {num_parts} parts successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def extracter(file_path: str, tab_type: str) -> str:
    try:
        def process(line):
            if ':' in line:
                return line
            return None
        process_file_line_by_line(file_path, 'extracted.txt', process)
        return "USER:PASS extracted successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def passuser(file_path: str, tab_type: str) -> str:
    try:
        def process(line):
            parts = line.strip().split(':', 1)
            if len(parts) == 2 and all(parts):
                return f"{parts[1]}:{parts[0]}\n"
            return None
        process_file_line_by_line(file_path, 'converted.txt', process)
        return "Converted USER:PASS to PASS:USER successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def soft_mix(file_path: str, tab_type: str) -> str:
    try:
        lines = []
        with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
            lines = list(infile)
        random.shuffle(lines)
        with open('mixed.txt', 'w', encoding='utf-8') as outfile:
            outfile.writelines(lines)
        return "Soft randomized successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def hard_mix(file_path: str, tab_type: str) -> str:
    try:
        with open(file_path, 'r', encoding='utf-8', errors='replace') as infile, \
             open('hard_mixed.txt', 'w', encoding='utf-8') as outfile:
            lines = list(infile)
            random.shuffle(lines)
            for line in lines:
                outfile.write(''.join(random.sample(line, len(line))))
        return "Hard randomized successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def domain_sorter(file_path: str, tab_type: str) -> str:
    try:
        lines = []
        with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
            for line in infile:
                if '@' in line and ':' in line:
                    user_part, rest = line.split('@', 1)
                    domain_pass = rest.split(':', 1)
                    if len(domain_pass) == 2:
                        domain = domain_pass[0]
                        lines.append((domain, line.strip()))
        sorted_lines = [line for _, line in sorted(lines)]
        with open('sorted_domains.txt', 'w', encoding='utf-8') as outfile:
            for line in sorted_lines:
                outfile.write(line + '\n')
        return "Domains sorted successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def split_by_domain(file_path: str, tab_type: str) -> str:
    try:
        domain_dict = defaultdict(list)
        with open(file_path, 'r', encoding='utf-8', errors='replace') as infile:
            for line in infile:
                if '@' in line and ':' in line:
                    user_part, rest = line.strip().split('@', 1)
                    domain, _ = rest.split(':', 1)
                    domain_dict[domain].append(line)
        for domain, lines in domain_dict.items():
            with open(f'{domain}_{os.path.basename(file_path)}', 'w', encoding='utf-8') as outfile:
                outfile.writelines(lines)
        return "Split by domain successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

def cc_extractor(file_path: str, tab_type: str) -> str:
    try:
        def process(line):
            if len(line.split()) >= 4:
                return line
            return None
        process_file_line_by_line(file_path, 'cc_extracted.txt', process)
        return "Credit card data extracted successfully.\n"
    except Exception as e:
        return f"Error: {str(e)}\n"

if __name__ == "__main__":
    if len(sys.argv) not in [4, 5]: 
        print("Usage: python backend.py <function> <file_path> <tab_type> [<num_parts> or <second_file_path>]")
        sys.exit(1)
    
    function_name = sys.argv[1]
    file_path = sys.argv[2]
    tab_type = sys.argv[3]

    functions = {
        "password_remover": lambda fp, tt: password_remover(fp, tt),
        "email_remover": lambda fp, tt: email_remover(fp, tt),
        "remove_duplicate": lambda fp, tt: remove_duplicate(fp, tt),
        "sort_lines": lambda fp, tt: sort_lines(fp, tt),
        "removelines": lambda fp, tt: removelines(fp, tt),
        "combine_combos": lambda fp, tt, sfp: combine_combos(fp, tt, sfp),
        "splitter": lambda fp, tt, np: splitter(fp, tt, np),
        "extracter": lambda fp, tt: extracter(fp, tt),
        "passuser": lambda fp, tt: passuser(fp, tt),
        "soft_mix": lambda fp, tt: soft_mix(fp, tt),
        "hard_mix": lambda fp, tt: hard_mix(fp, tt),
        "domain_sorter": lambda fp, tt: domain_sorter(fp, tt),
        "split_by_domain": lambda fp, tt: split_by_domain(fp, tt),
        "cc_extractor": lambda fp, tt: cc_extractor(fp, tt)
    }

    if function_name in functions:
        if function_name == "splitter":
            if len(sys.argv) != 5:
                print("Usage: python backend.py splitter <file_path> <tab_type> <num_parts>")
                sys.exit(1)
            print(functions[function_name](file_path, tab_type, int(sys.argv[4])))
        elif function_name == "combine_combos":
            if len(sys.argv) != 5:
                print("Usage: python backend.py combine_combos <file_path> <tab_type> <second_file_path>")
                sys.exit(1)
            print(functions[function_name](file_path, tab_type, sys.argv[4]))
        else:
            print(functions[function_name](file_path, tab_type))
    else:
        print(f"Function {function_name} not found.\n")
