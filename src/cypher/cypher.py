def build_pair_map(keyword: str) -> dict:

    pair_map: dict[str, str] = {}
    seen: set[str] = set()
    ALPHABET = "abcdefghijklmnopqrstuvwxyz"
    lower_keyword = keyword.lower()
    
    for letter in lower_keyword:
        if letter not in ALPHABET or letter in seen:
          continue # tipo o return em funcoes
        
        seen.add(letter)
        index = ALPHABET.index(letter)
        
        #? can do this instead: partner = ALPHABET[index + 1] if index < 25 else ALPHABET[24]
        if index == 25:
          partner = ALPHABET[24] # "z" -> "y"
        else:
          partner = ALPHABET[index + 1] # next letter: "a" -> "b", "n" -> "o", "l" -> "m"
        
        if partner not in pair_map:
          pair_map[letter] = partner # "a" -> "b"
          pair_map[partner] = letter # "b" -> "a"
       #* no dicionário pares, a letra é a chave e a parceira é o valor e vice-versa
       #* ex: pares["a"] = "b" e pares["b"] = "a"
       
    print(pair_map)
    return pair_map

  
def cipher(text: str, keyword: str) -> str:
    pair_map = build_pair_map(keyword)
    result = []
    
    for letter in text.lower():
      result.append(pair_map.get(letter, letter))
      #* returns letter when not found
    
    return "".join(result)

print(cipher("Hello, World!", "gato"))
