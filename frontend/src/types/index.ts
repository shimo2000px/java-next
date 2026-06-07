export type RecipeCategory = 'main_dish' | 'rice' | 'noodle';
export type StockCategory = 'rice' | 'main_dish' | 'side_dish' | 'noodle';

export interface Recipe {
  id: number;
  name: string;
  category: RecipeCategory;
  size?: string;
  memo?: string;
  createdAt: string;
}

export interface MealPlan {
  id: number;
  plannedDate: string;
  isRandom: boolean;
  recipe: Recipe | null;
}

export interface Stock {
  id: number;
  name: string;
  category: StockCategory;
  isAvailable: boolean;
  createdAt: string;
}

export interface BentoPlan {
  id: number;
  plannedDate: string;
  rice?: Stock | null;
  main?: Stock | null;
  side?: Stock | null;
  noodle?: Stock | null;
  memo?: string | null;
}
