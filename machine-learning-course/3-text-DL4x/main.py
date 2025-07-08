import os
import random
import re
from collections import defaultdict

import torch
import torch.nn as nn
from torch.utils.data import Dataset, DataLoader
from tokenizers import Tokenizer, models, trainers, pre_tokenizers, decoders
from tqdm import tqdm

seeds = [
    "Or to",
    "To grunt and sweat",
    "In the sky",
    "he married"
]


def train_tokenizer(tokenizer_path, text):
    tokenizer = Tokenizer(models.BPE(unk_token="[UNK]"))
    tokenizer.pre_tokenizer = pre_tokenizers.ByteLevel()

    trainer = trainers.BpeTrainer(
        special_tokens=[
            "[UNK]",
            "[EOS]",
            "[PAD]"
        ],
        vocab_size=5000
    )
    tokenizer.train_from_iterator([text], trainer)

    tokenizer.decoder = decoders.ByteLevel()
    tokenizer.save(tokenizer_path)

    return tokenizer


def prepare_data(tokenizer, text, context_size=64):
    text = ' '.join(text.split())
    encoded = tokenizer.encode(text + " [EOS]")
    token_ids = encoded.ids

    sequences = []
    for i in range(1, len(token_ids)):
        input_seq = token_ids[max(0, i - context_size):i]
        target = token_ids[i]
        sequences.append((input_seq, target))

    return sequences


class CharDataset(Dataset):
    def __init__(self, data, pad_id, context_size=64):
        self.data = data
        self.pad_id = pad_id
        self.context_size = context_size

    def __len__(self):
        return len(self.data)

    def __getitem__(self, idx):
        input_ids, label = self.data[idx]
        pad_len = self.context_size - len(input_ids)
        input_ids = [self.pad_id] * pad_len + input_ids

        return {
            'input_ids': torch.tensor(input_ids, dtype=torch.long),
            'labels': torch.tensor(label, dtype=torch.long)
        }


class LSTMModel(nn.Module):
    def __init__(self, vocab_size, embedding_dim=128, hidden_dim=256, num_layers=2):
        super().__init__()
        self.embedding = nn.Embedding(vocab_size, embedding_dim)
        self.lstm = nn.LSTM(embedding_dim, hidden_dim, num_layers, batch_first=True)
        self.fc = nn.Linear(hidden_dim, vocab_size)

    def forward(self, input_ids):
        x = self.embedding(input_ids)
        out, _ = self.lstm(x)
        logits = self.fc(out[:, -1, :])
        return logits


def generate_text(model, tokenizer, prompt, strategy="greedy", temperature=1.0, max_len=50):
    model.eval()
    device = next(model.parameters()).device

    ids = tokenizer.encode(prompt.lower()).ids
    eos_id = tokenizer.token_to_id("[EOS]")

    for _ in range(max_len):
        input_tensor = torch.tensor([ids[-64:]], dtype=torch.long).to(device)
        with torch.no_grad():
            logits = model(input_tensor)
            logits = logits / temperature

            if strategy == "greedy":
                next_id = torch.argmax(logits, dim=-1).item()
            elif strategy == "probabilistic":
                probs = torch.softmax(logits, dim=-1)
                next_id = torch.multinomial(probs, 1).item()
            else:
                raise ValueError("Invalid strategy")

        ids.append(next_id)
        if next_id == eos_id:
            break

    return tokenizer.decode(ids)


def lstm(text):
    model_path = 'output/lstm_model.pt'
    tokenizer_path = 'output/tokenizer.json'
    os.makedirs('output', exist_ok=True)

    tokenizer = train_tokenizer(tokenizer_path, text)
    data = prepare_data(tokenizer, text)
    vocab_size = tokenizer.get_vocab_size()

    dataset = CharDataset(data, pad_id=tokenizer.token_to_id("[PAD]") or 0)
    dataloader = DataLoader(dataset, batch_size=32, shuffle=True)

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model = LSTMModel(vocab_size).to(device)

    optimizer = torch.optim.Adam(model.parameters(), lr=1e-3)
    criterion = nn.CrossEntropyLoss()

    if os.path.exists(model_path):
        model.load_state_dict(torch.load(model_path))
        model.to(device)
    else:
        for epoch in range(30):
            model.train()
            total_loss = 0
            for batch in tqdm(dataloader, desc=f"Epoch {epoch + 1}"):
                input_ids = batch['input_ids'].to(device)
                labels = batch['labels'].to(device)

                logits = model(input_ids)
                loss = criterion(logits, labels)

                optimizer.zero_grad()
                loss.backward()
                optimizer.step()

                total_loss += loss.item()

            print(f"Epoch {epoch + 1}, loss = {total_loss / len(dataloader):.4f}")

        torch.save(model.state_dict(), model_path)

    for seed in seeds:
        print("\n[Greedy]")
        print(generate_text(model, tokenizer, seed, strategy='greedy'))

        print("\n[Probabilistic]")
        print(generate_text(model, tokenizer, seed, strategy='probabilistic', temperature=1.0))


class MarkovChain:
    def __init__(self):
        self.model = defaultdict(list)

    def train(self, text, n=2):
        tokens = text.split()
        for i in range(len(tokens) - n):
            prefix = tuple(tokens[i:i + n - 1])
            next_token = tokens[i + n - 1]
            self.model[prefix].append(next_token)

    def generate(self, seed, max_len=20, strategy="greedy"):
        tokens = seed.lower().split()
        n = len(next(iter(self.model)))

        for _ in range(max_len):
            prefix = tuple(tokens[-n:])
            options = self.model.get(prefix)

            if not options:
                break

            if strategy == "greedy":
                next_token = max(set(options), key=options.count)
            else:
                next_token = random.choice(options)

            tokens.append(next_token)

        return ' '.join(tokens)


def markov(text):
    cleaned_text = re.sub(r'\s+', ' ', text)

    mc = MarkovChain()
    mc.train(cleaned_text, n=2)

    for seed in seeds:
        print("\n[Greedy]")
        print(mc.generate(seed, strategy="greedy"))

        print("\n[Probabilistic]")
        print(mc.generate(seed, strategy="probabilistic"))


def main():
    text_path = 'dataset/input.txt'

    with open(text_path, 'r', encoding='utf-8') as f:
        text = f.read().lower()

    lstm(text)
    markov(text)


if __name__ == "__main__":
    main()
